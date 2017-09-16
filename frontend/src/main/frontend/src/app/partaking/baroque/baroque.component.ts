import {AfterViewInit, Component, EventEmitter, OnInit, Output} from "@angular/core";
import { CompetitionShortInfo} from "../CompetitionShortInfo";
import {DiscussionComponent} from "../../discussion/discussion.component";

@Component({
  selector: 'barogue-app',
  templateUrl: './baroque.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})
export class BaroqueComponent extends DiscussionComponent implements OnInit, AfterViewInit {

  ngOnInit(): void {
    this.competitionShortInfo.compType = CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE;
    this.competitionShortInfo.adminMode = false;
  }

  ngAfterViewInit(): void {
  }

  public isUserChooseJazzProgramm(): boolean {
    return this.competitionShortInfo.isUserChooseAlternativeProgramm();
  }

}
