import {Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'vote-jazz-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class VoteJazzComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ;
        this.competitionShortInfo.adminMode = false;
    }
}