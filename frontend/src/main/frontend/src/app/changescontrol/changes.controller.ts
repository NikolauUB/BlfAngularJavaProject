import {Injectable} from "@angular/core";
import {ChangesService} from "./changes.service";
import {ChangesKeywords} from "./ChangesKeywords";
import {DetailsController} from "./../auth/userdetails/details.controller";
import {ThreadChanges} from "./ThreadChanges";
import {UsersChanges} from "./UsersChanges";
import {Observable} from 'rxjs/Observable';
import { Ng2DeviceService  } from 'ng2-device-detector';


@Injectable()
export class ChangesController {
  public static PREVIOUS_TIME: string = "PREV_TIME";
  public static COMPETITION_LIST: string = "COMP_LIST";

  public static LSTORAGE_OBJECT_COUNT: number = 5;
  public static DESCRIPTION_CLASSIC: string = "DESC_0";
  public static DESCRIPTION_JAZZ: string = "DESC_1";
  public static DESCRIPTION_FREE: string = "DESC_2";
  public static DESCRIPTION_COMPOSITION: string = "DESC_3";
  public static DESCRIPTION_CONCERT: string = "DESC_4";
  public static DESCRIPTION_PREFIX: string = "DESC_";

  public static COMPETITION_MEMBERS_CLASSIC: string = "MBRS_0";
  public static COMPETITION_MEMBERS_JAZZ: string = "MBRS_1";
  public static COMPETITION_MEMBERS_FREE: string = "MBRS_2";
  public static COMPETITION_MEMBERS_COMPOSITION: string = "MBRS_3";
  public static COMPETITION_MEMBERS_CONCERT: string = "MBRS_4";
  public static COMPETITION_MEMBERS_PREFIX: string = "MBRS_";
  public static HIDE_PARTAKE_DISCUSS_PREFIX: string = "HD_";

  public static VOTING_CLASSIC: string = "V_0";
  public static VOTING_JAZZ: string = "V_1";
  public static VOTING_FREE: string = "V_2";
  public static VOTING_COMPOSITION: string = "V_3";
  public static VOTING_CONCERT: string = "V_4";
  public static VOTING_PREFIX: string = "V_";
  public static UNREAD_MESSAGES: string = "UNREAD_MSGS";

  //keywords which are not stored in client storage
  public static COMPETITION_MEMBERS_COUNT: string = "MBCNT_";
  public static VOTING_ITEMS_COUNT: string = "VCNT_";

  changesKeywords: ChangesKeywords;
  deviceInfo: any;

  constructor(private changesService: ChangesService,
              private userDetailsController: DetailsController,
              private deviceService: Ng2DeviceService) {
    this.deviceInfo = this.deviceService.getDeviceInfo();
  }

  public isBrowserVersionFittable(): boolean {
    if (this.deviceInfo.browser === 'safari') {
      return  (+this.deviceInfo.browser_version.split(".")[0] > 7);
    } else if (this.deviceInfo.browser === 'ie') {
      return  (+this.deviceInfo.browser_version.split(".")[0] > 9);
    } else if (this.deviceInfo.browser === 'opera') {
      return  (+this.deviceInfo.browser_version.split(".")[0] > 47);
    }
    return true;
  }

  public checkChangesInThread(thDate: Date, threadId: number): Promise<ThreadChanges> {
    return this.changesService
        .getThreadUpdates(thDate, threadId)
        .then(reply => {return reply;})
        .catch(e => this.handleError(e));
  }

  private writeInStorageChangesInActiveThreads( thDate: number): void {
      this.changesService
          .getActiveThreadUpdates(thDate)
          .then(reply => {
            var unreadMsgs = localStorage.getItem(ChangesController.UNREAD_MESSAGES);
            var jsonObject = (unreadMsgs != null) ? JSON.parse(unreadMsgs) : null;
            if (jsonObject === null || Object.keys(jsonObject).length == 0) {
               localStorage.setItem(ChangesController.UNREAD_MESSAGES, JSON.stringify(reply));
            } else if (Object.keys(reply).length > 0) {
              Object.keys(reply).forEach( key => {
                if(jsonObject.hasOwnProperty(key)) {
                  jsonObject[key] = jsonObject[key] + reply[key];
                } else {
                  jsonObject[key] = reply[key];
                }
              });
              localStorage.setItem(ChangesController.UNREAD_MESSAGES, JSON.stringify(jsonObject));
            }

          })
          .catch(e => this.handleError(e));
  }

  public checkUsersChanges(usrTime: Date): void {
    if(usrTime == null) return;
      this.changesService
          .getUsersUpdates(usrTime)
          .then(reply => this.cleanIndexedDBForUsers(reply))
          .catch(e => this.handleError(e));
  }
  public checkUsersOldBrowsers(usrTime: number): void {
    if(usrTime == null) return;
    this.changesService
        .getUsersUpdatesForOldBrowsers(usrTime)
        .then(reply => this.cleanVoteCache(reply))
        .catch(e => this.handleError(e));
  }

  private cleanVoteCache(reply: UsersChanges): void {
    if(reply.userIds && reply.userIds.length > 0) {
      localStorage.removeItem(ChangesController.VOTING_CLASSIC);
      localStorage.removeItem(ChangesController.VOTING_JAZZ);
      localStorage.removeItem(ChangesController.VOTING_FREE);
      localStorage.removeItem(ChangesController.VOTING_COMPOSITION);
      localStorage.removeItem(ChangesController.VOTING_CONCERT);
    }
  }

  private cleanIndexedDBForUsers(reply: UsersChanges): void {
    this.cleanVoteCache(reply);
    if (this.isBrowserVersionFittable()) {
      reply.userIds.forEach((userId)=> {
        this.userDetailsController.cleanUserDetails(userId);
      });
    }
  }



  public init(): Promise<void> {
    //clean previous
    //check updates forcompetition information
    this.changesKeywords = null;
    var prevTime  = localStorage.getItem(ChangesController.PREVIOUS_TIME);
    var time: number = (prevTime == null) ? -1: parseInt(prevTime, 10);
    var week: number = 604800000;
    if((new Date().getTime() - time) > week ) {
      //alert("Кэш отсутствует или старше недели. Производится полное обновление  (cache_time: " + time + "now: " + new Date().getTime() + ")");
      time = -1;
    }
    if (time === -1) {
      localStorage.removeItem(ChangesController.COMPETITION_LIST);
      localStorage.removeItem(ChangesController.DESCRIPTION_CLASSIC);
      localStorage.removeItem(ChangesController.DESCRIPTION_JAZZ);
      localStorage.removeItem(ChangesController.DESCRIPTION_FREE);
      localStorage.removeItem(ChangesController.DESCRIPTION_COMPOSITION);
      localStorage.removeItem(ChangesController.DESCRIPTION_CONCERT);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_CLASSIC);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_JAZZ);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_FREE);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_COMPOSITION);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_CONCERT);
      localStorage.removeItem(ChangesController.VOTING_CLASSIC);
      localStorage.removeItem(ChangesController.VOTING_JAZZ);
      localStorage.removeItem(ChangesController.VOTING_FREE);
      localStorage.removeItem(ChangesController.VOTING_COMPOSITION);
      localStorage.removeItem(ChangesController.VOTING_CONCERT);
    }

    this.writeInStorageChangesInActiveThreads(time);

    var checkResult = this.changesService
        .checkChanges(time)
        .then( reply => this.treatReply(reply, time))
        .catch(e => this.handleError(e));
    if (this.isBrowserVersionFittable()) {
      return checkResult;
    }
    return;
  }

  private treatReply(reply: any, time: number): Promise<void> {
    this.changesKeywords = reply;
    localStorage.setItem(ChangesController.PREVIOUS_TIME, this.changesKeywords.time.toString());
    //if time -1 just get server time only
    if (time === -1 && this.isBrowserVersionFittable()) {
      return this.userDetailsController.createStore();
    }
    this.changesKeywords.keywords.forEach(keyword => {
      if(keyword.startsWith(ChangesController.COMPETITION_MEMBERS_COUNT)) {
        this.checkCountAndClean(ChangesController.COMPETITION_MEMBERS_PREFIX,
            ChangesController.COMPETITION_MEMBERS_COUNT,
            keyword);
      } else if (keyword.startsWith(ChangesController.VOTING_ITEMS_COUNT)) {
        this.checkCountAndClean(ChangesController.VOTING_PREFIX,
            ChangesController.VOTING_ITEMS_COUNT,
            keyword);
      } else {
        localStorage.removeItem(keyword);
      }
    });
    if (this.isBrowserVersionFittable()) {
      return this.userDetailsController
          .createStoreAndGetMaxDate()
          .then(usrTime => {
            this.checkUsersChanges(usrTime);
          });
    } else {
      this.checkUsersOldBrowsers(time);
    }
  }

  private checkCountAndClean(objectTypePrefix: string, objectCountConst: string, keyword: string): void {
    var counter = 0;
    for (let i = 0; i < ChangesController.LSTORAGE_OBJECT_COUNT; i++) {
      let ls = localStorage.getItem(objectTypePrefix + i);
      if (objectTypePrefix ===  ChangesController.VOTING_PREFIX) {
        let acmObj = (ls) ? JSON.parse(ls) : null;
        counter += (acmObj && acmObj.voteData) ? acmObj.voteData.length : 0;
      } else {
        let acm:Array<any> = (ls) ? JSON.parse(ls) : new Array<any>();
        counter += acm.length;
      }
    }
    if (counter !== +keyword.split(objectCountConst)[1]) {
      for (let n = 0; n < ChangesController.LSTORAGE_OBJECT_COUNT; n++) {
        localStorage.removeItem(objectTypePrefix + n);
      }
    }
  }

  private handleError(e: any) : void {
    console.error(e.toString() || e.json().message );
    alert(e.toString() || e.json().message );
  }

}
