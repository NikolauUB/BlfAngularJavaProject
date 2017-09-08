import {Injectable} from "@angular/core";
import {AuthService} from "../auth.service";
import {AngularIndexedDB} from 'angular2-indexeddb';
import {UserData} from "../../model/auth/UserData";
import {DiscussionItem} from "app/model/DiscussionItem";

@Injectable()
export class DetailsController {
  private db: AngularIndexedDB = new AngularIndexedDB('UserDetails', 1);
  public static defaultAvatar: string = "../../assets/images/defaultAvatar.jpg";

  constructor(private authService: AuthService) {
    this.createStore();
  }

  public loadUserDetails(item: DiscussionItem): void {
    this.db.getByKey('userdetails', item.authorId)
        .then((details) => {
          if(details == null) {
            this.authService.getUserDetails(item.authorId)
              .then(reply => this.saveUserDetailsInDB(reply, item))
              .catch( e => alert(e));
          } else {
            item.authorUsername = details.username;
            item.authorAvatar = details.avatar;
          }
        }, (error) => {
          console.log(error);
        });
  }

  public updateUserDetails(userId: number): void {
    this.db.getByKey('userdetails', userId)
        .then((details) => {
          if(details == null) {
            this.authService.getUserDetails(userId)
                .then(reply => this.saveUserDetailsInDBbyId(reply, userId))
                .catch( e => alert(e));
          }
        }, (error) => {
          console.log(error);
        });
  }

  public cleanUserDetails(userId: number): void {
    this.db.delete('userdetails', userId).catch(e=> alert(e));
  }

  private saveUserDetailsInDB(reply:UserData, item: DiscussionItem) {
    item.authorUsername = reply.username;
    item.authorAvatar = reply.previewImage;
    this.saveUserDetailsInDBbyId(reply, item.authorId);
  }

  private saveUserDetailsInDBbyId(reply: UserData, userId: number) {
    this.db.add('userdetails',
        { id: userId,
          username: reply.username,
          avatar: reply.previewImage,
          created: reply.created,
          updated: reply.updated})
        .catch(e => alert(e));
  }

  public createStore(): void {
    this.db.createStore(1, (evt) => {
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
       var maxUpdated:Date = new Date(0);
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

}
