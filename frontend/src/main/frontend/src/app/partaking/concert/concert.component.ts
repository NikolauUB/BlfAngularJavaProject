import {AfterViewInit, Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../CompetitionShortInfo";
import {DiscussionComponent} from "../../discussion/discussion.component";

@Component({
  selector: 'concert-app',
  templateUrl: './concert.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class ConcertComponent extends DiscussionComponent implements OnInit, AfterViewInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_CONCERT;
    this.competitionShortInfo.adminMode = false;
  }

  ngAfterViewInit(): void {
  }
}
