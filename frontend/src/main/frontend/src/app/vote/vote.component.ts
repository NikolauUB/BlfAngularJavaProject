import {Component, OnInit, ViewChild} from '@angular/core';
import { Router }            from '@angular/router';

import { VoteData }                from '../model/VoteData';
import { VoteService }         from './vote.service';
import {CompetitionInfo} from "../model/CompetitionInfo";
import {AuthData} from "../model/auth/AuthData";
import {AuthService} from "../auth/auth.service";
import { CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import {PartakingService} from "../partaking/partaking.service";

@Component({
  selector: 'vote-app',
  templateUrl: './voting.component.html',
  styleUrls: [ './voting.component.css' ]
})
export class VoteComponent implements OnInit {
  voteInfo: CompetitionInfo = new CompetitionInfo;
  selectedItem: Set<VoteData> = new Set<VoteData>();
  startDate: Date;
  endDate: Date;
  errorMsg: string;

  constructor(
    private voteService: VoteService,
    private authService: AuthService,
    protected partakingService: PartakingService,
    protected competitionShortInfo: CompetitionShortInfo,
    private router: Router) {

  }

  getVoteInfo(): void {
    this.selectedItem = new Set<VoteData>();
    this.voteService
      .getVoteItems(this.competitionShortInfo.compType)
      .then(voteInfo => this.prepareView(voteInfo));
  }

  prepareView(voteInfo: CompetitionInfo): void {
    this.voteInfo = voteInfo;
    if (this.voteInfo.voted) {
      this.voteInfo.voteData.forEach(item => {
        if (item.order) this.selectedItem.add(item);
      });
      this.sortItems();
    }
  }



  ngOnInit(): void {
    if (!this.authService.getAuth()) {
      this.authService.init().then(e=>this.getVoteInfo());
    } else {
      this.getVoteInfo();
    }
  }

  public isAuthentificated(): boolean {
    if (!this.authService.getAuth()) {
      return false;
    } else {
      return this.authService.getAuth().auth;
    }
  }


  protected sendResult(): void {
    if(this.selectedItem.size > 0) {
      var selected:Array<VoteData> = JSON.parse(JSON.stringify(Array.from(this.selectedItem)));
      //clean unneeded info
      selected.forEach(item => {
        item.audioUrl = "";
        item.videoUrl = "";
        item.description = "";
        item.userIds = new Array<number>();
        item.composition = "";
        item.author = "";
        item.instrmnts = "";
      });
      this.voteService
        .vote(selected, this.authService.getAuth())
        .then(reply=>this.getVoteInfo())
        .catch(e=>this.handleError(e));
    } else {
      alert("Please select");
    }
  }

  protected deleteVoting(): void {
    this.voteService
      .deleteVote(this.voteInfo.competitionData.id, this.authService.getAuth())
      .then(reply=>this.getVoteInfo())
      .catch(e=>this.handleError(e));
  }

  showReply(reply: string): void {
    alert(reply);
  }

  onSelect(voteSelected: VoteData): void {
    if (this.selectedItem.has(voteSelected)) {
      this.deselectItem(voteSelected);
    } else {
      this.selectItem(voteSelected);
    }
  }

  private deselectItem(voteSelected: VoteData): void {
    this.voteInfo.voteData.forEach(item => {
      if (item.id === voteSelected.id) {
        item.order = null;
        this.selectedItem.delete(item);
      } else if(item.order && item.order > voteSelected.order) {
        item.order = item.order - 1;
      }
    });
    this.sortItems();
  }

  private selectItem(voteSelected: VoteData): void {
    this.voteInfo.voteData.forEach(item => {
      if (item.id === voteSelected.id) {
        item.order = this.selectedItem.size + 1;
        this.selectedItem.add(item);
      }
    });
    this.sortItems();
  }

  private sortItems(): void {
    this.voteInfo.voteData.sort((itemA, itemB) => {
      if ((!itemA.order && itemB.order) || (itemA.order && itemB.order && itemA.order > itemB.order)) return 1;
      else if ((!itemB.order && itemA.order) || (itemB.order && itemA.order && itemB.order > itemA.order)) return -1;
      else return 0;
    });
  }

  protected handleError(e: any) : void {
    if(e.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт!");
      this.router.navigateByUrl("login");
    } else {
      this.errorMsg = e.json().message || e.toString();
    }
  }

}
