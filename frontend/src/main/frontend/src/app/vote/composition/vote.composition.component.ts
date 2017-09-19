import {Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-composition-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class VoteCompositionComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_COMPOSITION;
        this.competitionShortInfo.adminMode = false;
    }
}