import {AfterViewInit, Component, OnInit} from "@angular/core";
import {CompetitionShortInfo} from "../CompetitionShortInfo";
import {DiscussionComponent} from "../../discussion/discussion.component";

@Component({
  selector: 'composition-app',
  templateUrl: './composition.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class CompositionComponent extends DiscussionComponent implements OnInit, AfterViewInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_COMPOSITION;
  }

  ngAfterViewInit(): void {
  }
}
