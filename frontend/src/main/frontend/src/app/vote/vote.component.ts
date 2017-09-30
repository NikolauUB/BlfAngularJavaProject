import {Component, OnInit, ViewChild} from '@angular/core';
import { Router }            from '@angular/router';

import { VoteData }                from '../model/VoteData';
import { VoteService }         from './vote.service';
import {CompetitionInfo} from "../model/CompetitionInfo";
import {AuthData} from "../model/auth/AuthData";
import {AuthService} from "../auth/auth.service";
import { CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import {PartakingService} from "../partaking/partaking.service";
import {ItemdetailsComponent} from "../modal/itemdetails.component";
import { DetailsController } from '../auth/userdetails/details.controller';
import {ChangesController} from "../changescontrol/changes.controller";
import { UserData } from '../model/auth/UserData';


@Component({
  selector: 'vote-app',
  templateUrl: './voting.component.html',
  styleUrls: [ './voting.component.css' ]
})
export class VoteComponent implements OnInit {
  @ViewChild(ItemdetailsComponent)
  detailsmodal: ItemdetailsComponent = new ItemdetailsComponent(this.detailsController, this.changesController);
  voteInfo: CompetitionInfo = new CompetitionInfo;
  emptyVoteData: Array<VoteData> = new Array<VoteData>();
  selectedItem: Set<VoteData> = new Set<VoteData>();
  userAvatarMap: Map<number, UserData> = new Map<number, UserData>();
  currentUserData: UserData;
  opinionsMode: boolean = false;
  browserCanWorkWithIndexedDB: boolean = false;
  isAllSelected: boolean = false;
  userItemId: number;
  errorMsg: string;

  constructor(
    private voteService: VoteService,
    private authService: AuthService,
    protected partakingService: PartakingService,
    protected competitionShortInfo: CompetitionShortInfo,
    private router: Router,
    private detailsController: DetailsController,
    private changesController: ChangesController) {


  }

  getVoteInfo(): void {
    this.selectedItem = new Set<VoteData>();
    var voteInfoJson: string = localStorage.getItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
    if (voteInfoJson != null) {
      this.prepareView(JSON.parse(voteInfoJson));
    } else {
      this.voteService
          .getVoteItems(this.competitionShortInfo.compType)
          .then(voteInfo => this.saveInLocalStorageAndPrepare(voteInfo));
    }

  }

  public loadUserAvatar(userId: number): string {
    if (this.userAvatarMap.has(userId)) {
      return this.userAvatarMap.get(userId).previewImage;
    } else {
      this.currentUserData = new UserData();
      (this.browserCanWorkWithIndexedDB)
      ? this.detailsController.loadUserDetailsById(userId, this.currentUserData)
      : this.detailsController.loadUserDetailByIdFromDB(userId, this.currentUserData);
      this.userAvatarMap.set(userId, this.currentUserData);
    }
  }

  public getGoToLoginBtnTitle(): string {
    return (this.opinionsMode)
      ? "Для участия в обсуждении необходимо зайти на сайт"
      : "Для сохранения результатов голосования необходимо зайти на сайт";
  }

  public getVoteDataArray(): Array<VoteData>{
    return (this.opinionsMode) ? this.emptyVoteData : this.voteInfo.voteData;
  }

  private saveInLocalStorageAndPrepare(voteInfo: CompetitionInfo): void {
    localStorage.setItem(
        ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType,
        JSON.stringify(voteInfo));
    this.prepareView(voteInfo);
  }


  private prepareView(voteInfo: CompetitionInfo): void {
    this.voteInfo = voteInfo;
    this.userItemId = null;
    this.findUserItemId();
    if (this.voteInfo.voted) {
      this.voteInfo.voteData.forEach(item => {
        if (item.order) this.selectedItem.add(item);
      });
      this.isAllSelected = this.checkIsAllSelected();
      this.sortItems();
    }
  }



  ngOnInit(): void {
    this.browserCanWorkWithIndexedDB = this.changesController.isBrowserVersionFittable();
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
        .then(reply=>{
            localStorage.removeItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
            this.getVoteInfo();
          })
        .catch(e=>this.handleError(e));
    } else {
      this.errorMsg = "Пожалуйста, выберите хотя бы одно исполнение!"
    }
  }

  anySelected(): boolean {
    return this.selectedItem.size > 0;
  }

  protected deleteVoting(): void {
    this.voteService
      .deleteVote(this.voteInfo.competitionData.id, this.authService.getAuth())
      .then(reply=>{
          localStorage.removeItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
          this.getVoteInfo();
        })
      .catch(e=>this.handleError(e));

  }


  onSelect(voteSelected: VoteData): void {
    this.errorMsg = "";
    if (this.voteInfo.voted || this.isUserPartake(voteSelected)) {
      return;
    }
    if (this.selectedItem.has(voteSelected)) {
      this.deselectItem(voteSelected);
    } else {
      this.selectItem(voteSelected);
    }
    this.isAllSelected = this.checkIsAllSelected();
  }

  showDetails(voteItem: VoteData): void {
    this.detailsmodal.showDetails(voteItem);
  }

  public isUserPartake(vote: VoteData): boolean {
    return (this.userItemId && vote.id === this.userItemId);
  }

  public getLastLetter(voteItem: VoteData): string {
    return (voteItem.usernames.length > 1)? "ы":"";
  }

  private findUserItemId(): void {
    this.voteInfo.voteData.forEach(item => {
      item.userIds.forEach(id => {
        if (this.authService.getAuth() && this.authService.getAuth().uId === id) {
          this.userItemId = item.id;
        }
      });
    });
  }


  public convertTimeToDate(time: any): string {
    var d = new Date(time);
    return d.getDate() + '.' + (d.getMonth()+1) + '.' + d.getFullYear();
  }

  public goToLogin(): void {
    this.router.navigate(["/login"], { queryParams: { returnUrl: this.router.url }});
  }

  private checkIsAllSelected(): boolean {
    var result = true;
    this.voteInfo.voteData.forEach( item => {
      if (!item.order) {
        result = false;
        return false; //exit from forEach
      }
    });
    return result;
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
