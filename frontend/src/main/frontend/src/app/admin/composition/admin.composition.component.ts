import {Component, OnInit} from "@angular/core";
import {AdminComponent} from "../admin.component";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";

@Component({
    selector: 'admin-composition-app',
    templateUrl: '../admin.top.component.html',
    styleUrls: [ '../../vote/voting.component.css' ]
})
export class AdminCompositionComponent extends AdminComponent implements OnInit {
    ngOnInit():void {
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_COMPOSITION;
        this.competitionShortInfo.adminMode = true;
        this.competitionShortInfo.adminModeUserThread = -1;
    }
}
