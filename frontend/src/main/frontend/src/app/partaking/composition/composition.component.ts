import {Component, OnInit} from "@angular/core";
import {CompetitionComponent} from "../competition.component";
import {CompetitionShortInfo} from "../CompetitionShortInfo";

@Component({
  selector: 'composition-app',
  templateUrl: './composition.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class CompositionComponent extends CompetitionComponent implements OnInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_COMPOSITION;
  }
}
