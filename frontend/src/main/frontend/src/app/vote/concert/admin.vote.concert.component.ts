import {Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {VoteComponent} from "../vote.component";

@Component({
    selector: 'admin-vote-concert-app',
    templateUrl: '../vote.common.html',
    styleUrls: [ '../voting.component.css' ]
})
export class AdminVoteConcertComponent extends VoteComponent implements OnInit {

    ngOnInit(): void {
        this.checkAccess();
        this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_CONCERT;
        this.competitionShortInfo.adminMode = true;
    }


    private checkAccess(): void {
        if(!this.isAdmin()) {
          this.router.navigateByUrl('error403');
        }
    }
    private isAdmin(): boolean {
      return this.isAutheticated() && this.authService.getAuth().uName === "NikolayUB";
    }

    private isAutheticated(): boolean {
      return (this.authService && this.authService.getAuth()) ? this.authService.getAuth().auth : false;
    }

}
