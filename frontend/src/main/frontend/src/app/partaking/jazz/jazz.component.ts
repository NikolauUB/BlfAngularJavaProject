import {AfterViewInit, Component, OnInit} from "@angular/core";
import {CompetitionComponent} from "../competition.component";
import {CompetitionShortInfo} from "../CompetitionShortInfo";
import {DiscussionComponent} from "../../discussion/discussion.component";

@Component({
  selector: 'jazz-app',
  templateUrl: './jazz.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class JazzComponent extends DiscussionComponent implements OnInit, AfterViewInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ;
  }

  ngAfterViewInit(): void {
  }

  public isUserChooseBaroqueProgramm(): boolean {
    return this.competitionShortInfo.isUserChooseAlternativeProgramm();
  }
}
