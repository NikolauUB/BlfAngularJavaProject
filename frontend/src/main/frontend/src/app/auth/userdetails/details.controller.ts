import {Injectable} from "@angular/core";
import {AuthService} from "../auth.service";
import {AngularIndexedDB} from 'angular2-indexeddb';
import {UserData} from "../../model/auth/UserData";
import {DiscussionItem} from "app/model/DiscussionItem";

@Injectable()
export class DetailsController {
  private db: AngularIndexedDB = new AngularIndexedDB('UserDetails', 1);
  public static defaultAvatar: string = "assets/images/defaultAvatar.jpg";
  userAvatarMap: Map<number, UserData> = new Map<number, UserData>();

  constructor(private authService: AuthService) {
  }

  public loadUserDetails(item: DiscussionItem): void {
    this.db.getByKey('userdetails', item.authorId)
      .then((details) => {
        if(details == null) {
          this.authService.getUserDetails(item.authorId)
              .then(reply => this.saveUserDetailsInDB(reply, item))
              .catch( e =>  console.log(e));
        } else {
          item.authorUsername = details.username;
          if(details.avatar) {
              item.authorAvatar = details.avatar;
          }
        }
      }, (error) => {
        console.log(error);
      });
  }

  public loadUserDetailFromDBForDiscuss(item: DiscussionItem): void {
    this.authService.getUserDetails(item.authorId)
        .then(reply => this.fillInDiscussItem(reply, item))
        .catch( e =>  console.log(e));
  }


  public loadUserDetailsById(userId: number, userData: UserData): void {
    this.db.getByKey('userdetails', userId)
        .then((details) => {
          if(details == null) {
            this.authService.getUserDetails(userId)
                .then(reply => {this.saveUserDetailsInDBbyId(reply, userId, userData);})
                .catch( e =>  console.log(e));
          } else {
            userData.username = details.username;
            if (details.avatar) {
                userData.previewImage = details.avatar;
            }
            userData.created = details.created;
          }
        }, (error) => {
          console.log(error);
        });
  }

  public loadUserDetailByIdFromDB(userId: number, userData: UserData): void {
    this.authService.getUserDetails(userId)
        .then(reply => {this.fillInUserData(reply, userData);})
        .catch( e =>  console.log(e));
  }

  public loadUserDetailInItemAndUserDataByIdFromDB(item: DiscussionItem): void {
    if (this.userAvatarMap.has(item.authorId)) {
      var usrData = this.userAvatarMap.get(item.authorId);
      if (usrData.previewImage) {
          item.authorAvatar = usrData.previewImage;
      }
      item.authorUsername = usrData.username;
      console.info("from Map" + item.authorId);
    } else {
      this.authService.getUserDetails(item.authorId)
          .then(reply => {

            this.userAvatarMap.set(item.authorId, reply);
            this.fillInDiscussItem(reply, item);
            console.info("from db" + item.authorId);
          })
          .catch(e =>  console.log(e));
    }
  }

  public updateUserDetails(userId: number): void {
    this.db.getByKey('userdetails', userId)
        .then((details) => {
          if(details == null) {
            this.authService.getUserDetails(userId)
                .then(reply => this.saveUserDetailsInDBbyId(reply, userId, null))
                .catch( e => console.log(e));
          }
        }, (error) => {
          console.log(error);
        });
  }

  public cleanUserDetails(userId: number): void {
    this.db.delete('userdetails', userId).catch(e=> console.log(e));
  }

  private fillInDiscussItem(reply:UserData, item: DiscussionItem) {
    item.authorUsername = reply.username;
    if (reply.previewImage) {
        item.authorAvatar = reply.previewImage;
    }
  }

  private saveUserDetailsInDB(reply:UserData, item: DiscussionItem) {
    this.fillInDiscussItem(reply, item);
    this.saveUserDetailsInDBbyId(reply, item.authorId, null);
  }

  private fillInUserData(reply: UserData, userData: UserData) {
    if (userData) {
      userData.username = reply.username;
      if (reply.previewImage) {
          userData.previewImage = reply.previewImage;
      }
      userData.created = reply.created;
    }
  }

  private saveUserDetailsInDBbyId(reply: UserData, userId: number, userData: UserData) {
    this.fillInUserData(reply, userData);
    this.db.add('userdetails',
        { id: userId,
          username: reply.username,
          avatar: reply.previewImage,
          created: reply.created,
          updated: reply.updated})
        .catch(e => console.log(e));
  }

  public createStore(): Promise<any> {
    return this.db.createStore(1, (evt) => {
      let objectStore = evt.currentTarget.result.createObjectStore(
        'userdetails', {keyPath: "id", autoIncrement: false});

      objectStore.createIndex("username", "username", {unique: true});
      objectStore.createIndex("avatar", "avatar", {unique: false});
      objectStore.createIndex("created", "created", {unique: false});
      objectStore.createIndex("updated", "updated", {unique: false});
    });
  }

  public getMaxUpdatedDate(): Promise<Date> {
     return this.db.getAll('userdetails').then((users) => {
       var maxUpdated:number = 0;
       users.forEach((user) =>{
         if (user.updated > maxUpdated) {
           maxUpdated = user.updated;
         }
       });
       return maxUpdated;
     }, (error) => {
       console.log(error);
       return null;
     });
  }

  public createStoreAndGetMaxDate(): Promise<Date> {
    return this.db.createStore(1, (evt) => {
      let objectStore = evt.currentTarget.result.createObjectStore(
          'userdetails', {keyPath: "id", autoIncrement: false});

      objectStore.createIndex("username", "username", {unique: true});
      objectStore.createIndex("avatar", "avatar", {unique: false});
      objectStore.createIndex("created", "created", {unique: false});
      objectStore.createIndex("updated", "updated", {unique: false});
    }).then(e=>this.getMaxUpdatedDate());
  }

}
