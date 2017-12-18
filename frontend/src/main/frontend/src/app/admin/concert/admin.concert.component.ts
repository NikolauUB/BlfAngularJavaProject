import {Component, OnInit} from "@angular/core";
import {AdminComponent} from "../admin.component";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";

@Component({
    selector: 'admin-concert-app',
    templateUrl: '../admin.top.component.html',
    styleUrls: [ '../../vote/voting.component.css' ]
})
export class AdminConcertComponent extends AdminComponent implements OnInit {
    ngOnInit(): void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_CONCERT;
        this.competitionShortInfo.adminMode = true;
        this.competitionShortInfo.adminModeUserThread = -1;
    }
}
