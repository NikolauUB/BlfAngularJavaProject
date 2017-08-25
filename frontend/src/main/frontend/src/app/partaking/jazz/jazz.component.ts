import {Component, OnInit} from "@angular/core";
import {CompetitionComponent} from "../competition.component";
import {CompetitionShortInfo} from "../CompetitionShortInfo";

@Component({
  selector: 'jazz-app',
  templateUrl: './jazz.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class JazzComponent extends CompetitionComponent implements OnInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ;
  }
}
