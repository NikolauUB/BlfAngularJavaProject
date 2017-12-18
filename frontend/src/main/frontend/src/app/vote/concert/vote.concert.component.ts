import {Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-concert-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class VoteConcertComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_CONCERT;
        this.competitionShortInfo.adminMode = false;
    }
}
