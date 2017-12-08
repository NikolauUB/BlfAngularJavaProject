import {Component, OnInit} from "@angular/core";
import {Params} from "@angular/router";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-baroque-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class ShowByidComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.route.params.forEach((params: Params) => {
           this.competitionShortInfo.compId = params['id'];
        });
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_SHOW_HISTORY;
        this.competitionShortInfo.adminMode = false;
    }
}
