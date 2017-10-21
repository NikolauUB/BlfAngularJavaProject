import { ChangesController } from '../../changescontrol/changes.controller';
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
  
  public loadUserDetails(userId: number, userData: UserData, changesController: ChangesController): void {
      if (changesController.isBrowserVersionFittable()) {
        this.db.getByKey('userdetails', userId)
            .then((details) => {
              if(details == null) {
                //console.info("from DB into indexedDB" + userId);
                this.authService.getUserDetails(userId)
                  .then(reply => this.saveUserDetailsInDBbyId(reply, userId, userData))
                  .catch( e =>  console.log(e));
              } else {
                //console.info("from IndexedDB" + userId);
                userData.username = details.username;
                if(details.avatar) {
                  userData.previewImage = details.avatar;
                }
              }
            }, (error) => {
              console.log(error);
            });
      } else {
        if (this.userAvatarMap.has(userId)) {
          this.fillInUserData(this.userAvatarMap.get(userId), userData);
          //console.info("from Map" + userId);
        } else {
          //console.info("from DB" + userId);
          this.authService.getUserDetails(userId)
            .then(reply => this.fillInUserDataInMap(reply, userData, userId))
            .catch( e =>  console.log(e));
        }
      }
  }

  public cleanUserDetails(userId: number): void {
    this.db.delete('userdetails', userId).catch(e=> console.log(e));
  }
  
  private getMaxUpdatedDate(): Promise<Date> {
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
  
  private fillInUserData(reply: UserData, userData: UserData) {
    if (userData) {
      userData.username = reply.username;
      if (reply.previewImage) {
          userData.previewImage = reply.previewImage;
      }
      userData.created = reply.created;
    }
  }
  
  private fillInUserDataInMap(reply: UserData, userData: UserData, userId: number) {
    if (userData) {
      this.fillInUserData(reply, userData);
      this.userAvatarMap.set(userId, userData);
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

}
