import {Injectable} from "@angular/core";
import {ChangesService} from "./changes.service";
import {ChangesKeywords} from "./ChangesKeywords";
import {DetailsController} from "./../auth/userdetails/details.controller";
import {ThreadChanges} from "./ThreadChanges";
import {UsersChanges} from "./UsersChanges";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ChangesController {
  public static PREVIOUS_TIME: string = "PREV_TIME";
  public static COMPETITION_LIST: string = "COMP_LIST";
  public static DESCRIPTION_CLASSIC: string = "DESC_0";
  public static DESCRIPTION_JAZZ: string = "DESC_1";
  public static DESCRIPTION_FREE: string = "DESC_2";
  public static DESCRIPTION_COMPOSITION: string = "DESC_3";
  public static DESCRIPTION_PREFIX: string = "DESC_";

  public static COMPETITION_MEMBERS_CLASSIC: string = "MBRS_0";
  public static COMPETITION_MEMBERS_JAZZ: string = "MBRS_1";
  public static COMPETITION_MEMBERS_FREE: string = "MBRS_2";
  public static COMPETITION_MEMBERS_COMPOSITION: string = "MBRS_3";
  public static COMPETITION_MEMBERS_PREFIX: string = "MBRS_";
  public static HIDE_PARTAKE_DISCUSS_PREFIX: string = "HD_";

  public static VOTING_CLASSIC: string = "V_0";
  public static VOTING_JAZZ: string = "V_1";
  public static VOTING_FREE: string = "V_2";
  public static VOTING_COMPOSITION: string = "V_3";
  public static VOTING_PREFIX: string = "V_";

  //keywords which are not stored in client storage
  public static COMPETITION_MEMBERS_COUNT: string = "MBCNT_";
  public static VOTING_ITEMS_COUNT: string = "VCNT_";

  changesKeywords: ChangesKeywords;

  constructor(private changesService: ChangesService,
              private userDetailsController: DetailsController) {
  }

  public checkChangesInThread( thDate: Date, threadId: number): Promise<ThreadChanges> {
    return this.changesService
        .getThreadUpdates(thDate, threadId)
        .then(reply => {return reply;})
        .catch(e => this.handleError(e));
  }

  public checkUsersChanges(usrTime: Date): void {
    if(usrTime == null) return;
    this.changesService
        .getUsersUpdates(usrTime)
        .then(reply => this.cleanIndexedDBForUsers(reply))
        .catch(e => this.handleError(e));
  }

  private cleanIndexedDBForUsers(reply: UsersChanges): void {
    reply.userIds.forEach((userId)=>{
      this.userDetailsController.cleanUserDetails(userId);
    });
  }



  public init(): Promise<void> {
    //clean previous
    //check updates forcompetition information
    this.changesKeywords = null;
    var prevTime  = localStorage.getItem(ChangesController.PREVIOUS_TIME);
    var time: number = (prevTime == null) ? -1: parseInt(prevTime, 10);
    if (time === -1) {
      localStorage.removeItem(ChangesController.COMPETITION_LIST);
      localStorage.removeItem(ChangesController.DESCRIPTION_CLASSIC);
      localStorage.removeItem(ChangesController.DESCRIPTION_JAZZ);
      localStorage.removeItem(ChangesController.DESCRIPTION_FREE);
      localStorage.removeItem(ChangesController.DESCRIPTION_COMPOSITION);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_CLASSIC);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_JAZZ);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_FREE);
      localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_COMPOSITION);
      localStorage.removeItem(ChangesController.VOTING_CLASSIC);
      localStorage.removeItem(ChangesController.VOTING_JAZZ);
      localStorage.removeItem(ChangesController.VOTING_FREE);
      localStorage.removeItem(ChangesController.VOTING_COMPOSITION);
    }
    return this.changesService
        .checkChanges(time)
        .then( reply => this.treatReply(reply))
        .catch(e => this.handleError(e));

  }

  private treatReply(reply: any): Promise<void> {
    this.changesKeywords = reply;
    localStorage.setItem(ChangesController.PREVIOUS_TIME, this.changesKeywords.time.toString());
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


    //check updates for users
    return this.userDetailsController
          .createStoreAndGetMaxDate()
          .then(usrTime => {
            this.checkUsersChanges(usrTime);
          });

  }

  private checkCountAndClean(objectTypePrefix: string, objectCountConst: string, keyword: string): void {
    var counter = 0;
    for (let i = 0; i < 4; i++) {
      let ls = localStorage.getItem(objectTypePrefix + i);
      let acm:Array<any> = (ls) ? JSON.parse(ls) : new Array<any>();
      counter += acm.length;
    }
    if (counter > +keyword.split(objectCountConst)[1]) {
      for (let n = 0; n < 4; n++) {
        localStorage.removeItem(objectTypePrefix + n);
      }
    }
  }

  private handleError(e: any) : void {
    console.error(e.json().message || e.toString());
    alert(e.json().message || e.toString());
  }

}
