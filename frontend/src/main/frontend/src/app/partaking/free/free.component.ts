import {Component, OnInit} from "@angular/core";
import {CompetitionComponent} from "../competition.component";
import {CompetitionShortInfo} from "../CompetitionShortInfo";

@Component({
  selector: 'free-app',
  templateUrl: './free.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})

export class FreeComponent extends CompetitionComponent implements OnInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_FREE;
  }
}
