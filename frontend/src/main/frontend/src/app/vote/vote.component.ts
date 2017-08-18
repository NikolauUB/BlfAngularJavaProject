import {Component, OnInit, ViewChild} from '@angular/core';
import { Router }            from '@angular/router';

import { VoteData }                from '../model/VoteData';
import { VoteService }         from './vote.service';
import {AppModalComponent} from "../modal/appmodal.component";
import {CompetitionInfo} from "../model/CompetitionInfo";
import {AuthData} from "../model/auth/AuthData";
import {LoginData} from "../model/auth/LoginData";
import {AuthService} from "../auth/auth.service";

@Component({
  selector: 'my-app',
  templateUrl: './voting.component.html',
  styleUrls: [ './voting.component.css' ]
})
export class VoteComponent implements OnInit {
  @ViewChild(AppModalComponent)
  modal: AppModalComponent = new AppModalComponent();
  voteInfo: CompetitionInfo;
  authInfo: AuthData;
  selectedItem: Set<VoteData> = new Set<VoteData>();

  constructor(
    private voteService: VoteService,
    private authService: AuthService,
    private router: Router) {
    this.authInfo = this.authService.getAuth();
  }

  getVoteInfo(): void {
    this.voteService
      .getVoteItems(0)
      .then(voteInfo => this.voteInfo = voteInfo);
  }

  ngOnInit(): void {
    this.authInfo = this.authService.getAuth();
    this.getVoteInfo();
  }

  sendResult(): void {
    if(this.selectedItem.size > 0) {
      this.voteService
        .vote(Array.from(this.selectedItem), this.authInfo)
        .then(reply => this.showReply(reply));
    } else {
      this.modal.showModal("Please select");
    }
  }

  showReply(reply: string): void {
    this.modal.showModal(reply);
  }

  onSelect(voteSelected: VoteData): void {
    if (this.selectedItem.has(voteSelected)) {
      this.selectedItem.delete(voteSelected);
    } else {
      this.selectedItem.add(voteSelected);
    }
  }

}
