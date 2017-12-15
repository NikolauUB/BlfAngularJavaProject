import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {CompetitionList} from "../../model/CompetitionList";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {ChangesController} from "../../changescontrol/changes.controller";
import {ActiveCompetitions} from "../../model/ActiveCompetitions";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-menu-app',
    templateUrl: './vote.menu.component.html',
    styleUrls: [ '../voting.component.css' ]
})
export class VoteMenuComponent extends VoteComponent implements OnInit {
    competitionList: CompetitionList = new CompetitionList();


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
          switch (competition.type) {
            case CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE: {
              this.competitionList.hasBaroque = true;
              this.competitionList.baroqueStart = competition.start;
              this.competitionList.baroqueEnd = competition.end;
              break;
            }
            case CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ: {
              this.competitionList.hasJazz = true;
              this.competitionList.jazzStart = competition.start;
              this.competitionList.jazzEnd = competition.end;
              break;
            }
            case CompetitionShortInfo.TYPE_FREE: {
              this.competitionList.hasFree = true;
              this.competitionList.freeStart = competition.start;
              this.competitionList.freeEnd = competition.end;
              break;
            }
            case CompetitionShortInfo.TYPE_COMPOSITION: {
              this.competitionList.hasComposition = true;
              this.competitionList.compositionStart = competition.start;
              this.competitionList.compositionEnd = competition.end;
              break;
            }
            case CompetitionShortInfo.TYPE_CONCERT: {
              this.competitionList.hasConcert = true;
              this.competitionList.concertStart = competition.start;
              this.competitionList.concertEnd = competition.end;
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
}
