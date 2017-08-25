import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from "@angular/core";
import {CompetitionData} from "../model/CompetitionData";
import {PartakingService} from "./partaking.service";
import {Router} from "@angular/router";
import { CompetitionShortInfo} from "./CompetitionShortInfo";
import {ChangesController} from "../changescontrol/changes.controller";
import {AuthService} from "../auth/auth.service";
import {CompetitionMember} from "../model/CompetitionMember";

@Component({
  selector: 'competition-app',
  templateUrl: './competition.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class CompetitionComponent  implements OnInit{
  @ViewChild('embeddedVideo')
  embeddedVideo: any;
  private competitionData: CompetitionData;
  public membersForShow: Array<CompetitionMember> = new Array<CompetitionMember>();
  protected errorMsg: string;

  constructor(protected partakingService: PartakingService,
              protected authService: AuthService,
              protected router: Router,
              protected competitionShortInfo: CompetitionShortInfo) {

  }

  ngOnInit(): void {
    var compDesc: string = localStorage.getItem(ChangesController.DESCRIPTION_PREFIX + this.competitionShortInfo.compType);
    if (compDesc != null) {
       this.competitionData = JSON.parse(compDesc);
       this.competitionShortInfo.compId = this.competitionData.id;
    } else {
      this.loadCompetition();
    }

    var members: string = localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + this.competitionShortInfo.compType);

    if( members != null) {
      this.membersForShow = JSON.parse(members);
    } else {
      this.loadCompetitionsMembers();
    }
  }

  public reloadMembers(): void {
    this.membersForShow = new Array<CompetitionMember>();
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
    this.embeddedVideo.src = this.competitionData.sampleUrl;
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
    reply.forEach( (member)=> {
      if (!membersForShowMap.has(member.compType)) {
        membersForShowMap.set(member.compType, new Array<CompetitionMember>());
      }
      membersForShowMap.get(member.compType).push(member);

    });
    var possibleVariants = [CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE,
                    CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ,
                    CompetitionShortInfo.TYPE_FREE,
                    CompetitionShortInfo.TYPE_COMPOSITION];
    for (var variant of possibleVariants) {
      if (membersForShowMap.has(variant)) {
        localStorage.setItem(ChangesController.COMPETITION_MEMBERS_PREFIX + variant, JSON.stringify(membersForShowMap.get(variant)));
      } else {
        localStorage.removeItem(ChangesController.COMPETITION_MEMBERS_PREFIX + variant);
      }

      if (variant === this.competitionShortInfo.compType) {
        if ( membersForShowMap.has(variant) ) {
          this.membersForShow = membersForShowMap.get(variant);
        } else {
          this.membersForShow= new Array<CompetitionMember>();
        }
      }
    }
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
