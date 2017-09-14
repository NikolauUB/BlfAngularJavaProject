import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from "@angular/core";
import {CompetitionData} from "../model/CompetitionData";
import {PartakingService} from "./partaking.service";
import {Router} from "@angular/router";
import { CompetitionShortInfo} from "./CompetitionShortInfo";
import {ChangesController} from "../changescontrol/changes.controller";
import {ThemeController} from "../theme/theme.controller";
import {AuthService} from "../auth/auth.service";
import {CompetitionMember} from "../model/CompetitionMember";
import {DetailsController} from "../auth/userdetails/details.controller";

@Component({
  selector: 'competition-app',
  templateUrl: './competition.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class CompetitionComponent  implements  OnInit {
  private competitionData: CompetitionData = new CompetitionData();
  protected errorMsg: string;

  constructor(protected partakingService: PartakingService,
              protected authService: AuthService,
              protected router: Router,
              protected competitionShortInfo: CompetitionShortInfo,
              protected userDetailsController: DetailsController,
              protected changesController: ChangesController,
              protected themeController: ThemeController) {

  }


  ngOnInit(): void {
    this.init();
    if (!this.authService.getAuth()) {
      this.authService.init().then(e=>this.init());
    }
  }

  private init(): void {
    var compDesc: string = localStorage.getItem(ChangesController.DESCRIPTION_PREFIX + this.competitionShortInfo.compType);
    if (compDesc != null) {
       this.competitionData = JSON.parse(compDesc);
       this.competitionShortInfo.compId = this.competitionData.id;
    } else {
      this.competitionShortInfo.userThread = -1;
      this.loadCompetition();
    }

    var members: string = localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + this.competitionShortInfo.compType);
    if( members != null) {
      this.initUserPartakeState(members);
    } else {
      this.loadCompetitionsMembers();
    }
  }

  private initUserPartakeState(members: string) {
    this.competitionShortInfo.membersForShow = JSON.parse(members);
    if (this.authService.getAuth() != null){
      var member = this.competitionShortInfo.membersForShow.filter( (item) => {
        return item.mId === this.authService.getAuth().userId;
      });
      this.competitionShortInfo.userThread = (member.length > 0)? member[0].threadId : -1;
      if (this.competitionShortInfo.userThread === -1 && this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE) {
        var membersJazz: string = localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ);
	var jazzMember = null;
	if (membersJazz != null) {
          jazzMember = JSON.parse(membersJazz).filter( (item) => {
            return item.mId === this.authService.getAuth().userId;
          });
	}
        this.competitionShortInfo.userChoosePrescribeProgramm = (jazzMember!= null && jazzMember.length > 0) ? CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ: -1;
      } else if (this.competitionShortInfo.userThread === -1 && this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ) {
        var membersBar: string = localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE);
	var barMember = null;
	if (barMember != null) {
          barMember = JSON.parse(membersBar).filter( (item) => {
            return item.mId === this.authService.getAuth().userId;
          });
	}
        this.competitionShortInfo.userChoosePrescribeProgramm = (barMember != null && barMember.length > 0) ? CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE: -1;
      } else {
        this.competitionShortInfo.userChoosePrescribeProgramm =  this.competitionShortInfo.compType;
      }
    }
  }

  public reloadMembers(): void {
    this.loadCompetitionsMembers();
  }

  public loadCompetition(): void {
    this.partakingService
      .getCompetitionData(this.competitionShortInfo.compType)
      .then( reply => this.initCompetitionDesc(reply))
      .catch(e => this.handleError(e))
  }

  private initCompetitionDesc(reply:any): void{
    this.competitionData = reply;
    this.competitionShortInfo.compId = this.competitionData.id;
    localStorage.setItem(
      ChangesController.DESCRIPTION_PREFIX + this.competitionShortInfo.compType,
      JSON.stringify(this.competitionData));
  }

  public loadCompetitionsMembers(): void {
    this.partakingService
      .getCompetitionsMembers()
      .then(reply => this.handleMembersReply(reply))
      .catch(e => this.handleError(e));
  }

  private handleMembersReply(reply: Array<CompetitionMember>) {
    var membersForShowMap: Map<number, Array<CompetitionMember>> = new Map<number, Array<CompetitionMember>>();
    this.competitionShortInfo.userChoosePrescribeProgramm = -1;
    reply.forEach( (member)=> {
      if (!membersForShowMap.has(member.compType)) {
        membersForShowMap.set(member.compType, new Array<CompetitionMember>());
      }
      membersForShowMap.get(member.compType).push(member);

      /*if (this.authService != null
          && this.authService.getAuth() != null
          && member.mId === this.authService.getAuth().userId) {
        if (member.compType === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE || member.compType === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ) {
          this.competitionShortInfo.userChoosePrescribeProgramm = member.compType;
        }
        if  (member.compType === this.competitionShortInfo.compType) {
          this.competitionShortInfo.userThread = member.threadId;
        }
      }*/

    });
    var possibleVariants = [CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE,
                    CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ,
                    CompetitionShortInfo.TYPE_FREE,
                    CompetitionShortInfo.TYPE_COMPOSITION];
    for (var variant of possibleVariants) {
      if (membersForShowMap.has(variant)) {
        localStorage.setItem(ChangesController.COMPETITION_MEMBERS_PREFIX + variant, JSON.stringify(membersForShowMap.get(variant)));
      } else {
        localStorage.setItem(ChangesController.COMPETITION_MEMBERS_PREFIX + variant, JSON.stringify(new Array<CompetitionMember>()));
      }

      if (variant === this.competitionShortInfo.compType) {
        if ( membersForShowMap.has(variant) ) {
          this.competitionShortInfo.membersForShow = membersForShowMap.get(variant);
        } else {
          this.competitionShortInfo.membersForShow= new Array<CompetitionMember>();
        }
      }
    }

    this.initUserPartakeState(
        localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + this.competitionShortInfo.compType)
    );

  }



  public isBaroque(): boolean {
    return (this.competitionData
            && this.competitionData.type === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE);
  }
  public isJazz(): boolean {
    return (this.competitionData
      && this.competitionData.type === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ);
  }
  public isFree(): boolean {
    return (this.competitionData
      && this.competitionData.type === CompetitionShortInfo.TYPE_FREE);
  }
  public isComposition(): boolean {
    return (this.competitionData
      && this.competitionData.type === CompetitionShortInfo.TYPE_COMPOSITION);
  }

  protected handleError(e: any) : void {
    if(e.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт!");
      this.router.navigateByUrl("login");
    } else {
      this.errorMsg = e.json().message || e.toString();
    }
  }
}
