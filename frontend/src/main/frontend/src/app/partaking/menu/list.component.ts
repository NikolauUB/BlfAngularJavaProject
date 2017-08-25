import {Component, OnInit} from "@angular/core";
import {PartakingService} from "../partaking.service";
import {Router} from "@angular/router";
import {CompetitionList} from "../../model/CompetitionList";
import {CompetitionShortInfo} from "../CompetitionShortInfo";
import {ChangesController} from "../../changescontrol/changes.controller";
import {ActiveCompetitions} from "../../model/ActiveCompetitions";

@Component({
  selector: 'competition-list-app',
  templateUrl: './list.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})
export class ListComponent implements OnInit {
  competitionList: CompetitionList = new CompetitionList();
  errorMsg: string;

  constructor(protected partakingService: PartakingService,
              protected router: Router) {

  }

  ngOnInit(): void {
    let savedList = localStorage.getItem(ChangesController.COMPETITION_LIST);
    if(savedList !== null) {
      this.competitionList = JSON.parse(savedList);
    } else {
      this.loadActiveCompetitions();
    }

  }

  private loadActiveCompetitions(): void {
    this.partakingService
      .getActiveCompetitions()
      .then( reply => this.handleCompetitions(reply))
      .catch(e => this.handleError(e));
  }

  private handleCompetitions(reply: ActiveCompetitions): void {
      for (let competition of  reply.types) {
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
  }

  private handleError(e: any) : void {
      this.errorMsg = e.toString();
  }

}
