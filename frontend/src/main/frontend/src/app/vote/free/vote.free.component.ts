import {Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-free-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class VoteFreeComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_FREE;
        this.competitionShortInfo.adminMode = false;
    }
}