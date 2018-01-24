import {Component, OnInit, ViewChild} from "@angular/core";
import {Router} from "@angular/router";
import { UserStatisticHistory } from '../model/votestatistic/UserStatisticHistory';
import { UserCompetition } from '../model/votestatistic/UserCompetition';
import { NewstatisticService } from './newstatistic.service';
import {ShowpictureComponent} from "../modal/showpicture.component";
@Component({
    selector: 'newstatistic-app',
    templateUrl: './newstatistic.component.html',
    styleUrls: [ '../vote/voting.component.css' ]
})
export class NewstatisticComponent implements OnInit {
  @ViewChild(ShowpictureComponent)
  modal:ShowpictureComponent = new ShowpictureComponent();
  statistic: Array<UserStatisticHistory> = new Array<UserStatisticHistory>();
  errorMsg: string = "";
  selectedCompId: number;

  constructor(protected newstatisticService: NewstatisticService,
                protected router: Router) {
  }

  ngOnInit(): void {
    this.loadStatistic();
  }

  private loadStatistic(): void {
    this.newstatisticService.getAllStatistic()
        .then( reply => {this.statistic = reply;} )
        .catch(e => this.handleError(e));
  }

  public getStatistic(): Array<UserStatisticHistory> {
    return this.statistic;
  }

  public getCompetitionName(item: UserCompetition): string {
     var d = new Date(item.start);
     return item.name + ' - ' + d.toLocaleDateString("ru-RU", {month: 'long', year: 'numeric' });
  }

  public showCompetition(): void {
    this.router.navigateByUrl("showByid/" + this.selectedCompId);
  }


  private handleError(e: any) : void {
      if(e.status === 403) {
        alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт!");
        this.router.navigateByUrl("login");
      } else {
        this.errorMsg = e.json().message || e.toString();
      }
  }




  public showVenok(path:string):void {
      this.modal.showPicture(path);
  }

}
