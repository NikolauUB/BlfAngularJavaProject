import {AfterViewInit, Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../CompetitionShortInfo";
import {DiscussionComponent} from "../../discussion/discussion.component";

@Component({
  selector: 'free-app',
  templateUrl: './free.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class FreeComponent extends DiscussionComponent implements OnInit, AfterViewInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_FREE;
  }

  ngAfterViewInit(): void {
  }
}
