import {ChangesController} from '../changescontrol/changes.controller';
import { ActiveCompetitions } from '../model/ActiveCompetitions';
import {CompetitionData} from '../model/CompetitionData';
import { CompetitionList } from '../model/CompetitionList';
import { CompetitionShortInfo } from '../partaking/CompetitionShortInfo';
import {PartakingService} from '../partaking/partaking.service';
import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'welcome-app',
  templateUrl: './welcome.component.html',
  styleUrls: ['../vote/voting.component.css']
})
export class WelcomeComponent implements OnInit {

  competitionList: CompetitionList;

  obStartDate: Date;
  obEndDate: Date;
  freeStartDate: Date;
  freeEndDate: Date;
  compositionStartDate: Date;
  compositionEndDate: Date;

  currentDate: Date = new Date();
  errorMsg: string;

  constructor(protected partakingService: PartakingService) {

  }

  ngOnInit(): void {
    this.currentDate = new Date();
    var savedList = localStorage.getItem(ChangesController.COMPETITION_LIST);
    if (savedList !== null) {
      this.competitionList = JSON.parse(savedList);
      this.init();
    } else {
      this.loadActiveCompetitions();
    }
  }

  private init(): void {
    var compDesc: string;
    if (this.competitionList.hasBaroque || this.competitionList.hasJazz) {
      compDesc = (this.competitionList.hasBaroque) ? localStorage.getItem(ChangesController.DESCRIPTION_CLASSIC) : localStorage.getItem(ChangesController.DESCRIPTION_JAZZ);
      (compDesc != null) 
        ? this.setDates(JSON.parse(compDesc)) 
        : ((this.competitionList.hasBaroque) ? this.loadCompetition(CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE) : this.loadCompetition(CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ));
    }
    if (this.competitionList.hasFree) {
      compDesc = localStorage.getItem(ChangesController.DESCRIPTION_FREE);
      (compDesc != null) ? this.setDates(JSON.parse(compDesc)) : this.loadCompetition(CompetitionShortInfo.TYPE_FREE);
    }
    if (this.competitionList.hasComposition) {
      compDesc = localStorage.getItem(ChangesController.DESCRIPTION_COMPOSITION);
      (compDesc != null) ? this.setDates(JSON.parse(compDesc)) : this.loadCompetition(CompetitionShortInfo.TYPE_COMPOSITION);
    }
  }

  isObStarted(): boolean {
    return (this.obStartDate) ? this.obStartDate < this.currentDate : false;
  }

  isObEnded(): boolean {
    return (this.obEndDate) ? this.obEndDate < this.currentDate : false;
  }

  isFreeStarted(): boolean {
    return (this.freeStartDate) ? this.freeStartDate < this.currentDate : false;
  }

  isFreeEnded(): boolean {
    return (this.freeEndDate) ? this.freeEndDate < this.currentDate : false;
  }

  isCompositionStarted(): boolean {
    return (this.compositionStartDate) ? this.compositionStartDate < this.currentDate : false;
  }

  isCompositionEnded(): boolean {
    return (this.compositionEndDate) ? this.compositionEndDate < this.currentDate : false;
  }
  public convertTimeToDate(time: any): string {
        var d = new Date(time);
        return d.getDate() + '.' + (d.getMonth()+1) + '.' + d.getFullYear();
   }

  private loadCompetition(compType: number): void {
    this.partakingService
      .getCompetitionData(compType)
      .then(reply => this.treatReply(reply))
      .catch(e => this.handleError(e))
  }

  private treatReply(reply: CompetitionData): void {
    localStorage.setItem(
      ChangesController.DESCRIPTION_PREFIX + reply.type,
      JSON.stringify(reply));
    this.setDates(reply);
  }

  private setDates(reply: CompetitionData): void {
    if (reply.type === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE || reply.type === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ) {
      this.obStartDate = new Date(reply.start);
      this.obEndDate = new Date(reply.end);
    } else if (reply.type === CompetitionShortInfo.TYPE_FREE) {
      this.freeStartDate = new Date(reply.start);
      this.freeEndDate = new Date(reply.end);
    } else if (reply.type === CompetitionShortInfo.TYPE_COMPOSITION) {
      this.compositionStartDate = new Date(reply.start);
      this.compositionEndDate = new Date(reply.end);
    }
  }


  private loadActiveCompetitions(): void {
    this.partakingService
      .getActiveCompetitions()
      .then(reply => this.handleCompetitions(reply))
      .catch(e => this.handleError(e));
  }

  private handleCompetitions(reply: ActiveCompetitions): void {
    for (let competition of reply.types) {
      switch (competition) {
        case CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE: {
          this.competitionList.hasBaroque = true;
          break;
        }
        case CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ: {
          this.competitionList.hasJazz = true;
          break;
        }
        case CompetitionShortInfo.TYPE_FREE: {
          this.competitionList.hasFree = true;
          break;
        }
        case CompetitionShortInfo.TYPE_COMPOSITION: {
          this.competitionList.hasComposition = true;
          break;
        }
        default: {
          break;
        }
      }
    }
    //save in local storage
    localStorage.setItem(ChangesController.COMPETITION_LIST, JSON.stringify(this.competitionList));
    this.init();
  }

  private handleError(e: any): void {
    this.errorMsg = (e.json()) ? e.json().message : e.toString();
  }

}