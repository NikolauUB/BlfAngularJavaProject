import {Component, OnInit, OnDestroy, ViewChild, HostListener} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {VoteData} from '../model/VoteData';
import {VoteService} from './vote.service';
import {CompetitionInfo} from "../model/CompetitionInfo";
import {AuthData} from "../model/auth/AuthData";
import {AuthService} from "../auth/auth.service";
import {CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import {PartakingService} from "../partaking/partaking.service";
import {ItemdetailsComponent} from "../modal/itemdetails.component";
import {DetailsController} from '../auth/userdetails/details.controller';
import {ChangesController} from "../changescontrol/changes.controller";
import {UserData} from '../model/auth/UserData';
import { VoteStatistic } from '../model/votestatistic/VoteStatistic';
import { VoterRecord } from '../model/votestatistic/VoterRecord';


@Component({
  selector: 'vote-app',
  templateUrl: './voting.component.html',
  styleUrls: ['./voting.component.css']
})
export class VoteComponent implements OnInit,  OnDestroy {
  @ViewChild(ItemdetailsComponent)
  detailsmodal: ItemdetailsComponent = new ItemdetailsComponent(this.detailsController, this.changesController);
  voteInfo: CompetitionInfo = new CompetitionInfo;
  emptyVoteData: Array<VoteData> = new Array<VoteData>();
  voteStatistic: VoteStatistic = new VoteStatistic();
  selectedItem: Set<VoteData> = new Set<VoteData>();
  userAvatarMap: Map<number, UserData> = new Map<number, UserData>();
  currentUserData: UserData;
  opinionsMode: boolean = false;
  statisticMode: boolean = false;
  isAllSelected: boolean = false;
  userItemId: number;
  errorMsg: string;
  currentDate: Date = new Date();
  startDate: Date;
  endDate: Date;
  notSaved:boolean = false;


  constructor(
    private voteService: VoteService,
    protected authService: AuthService,
    protected partakingService: PartakingService,
    protected competitionShortInfo: CompetitionShortInfo,
    protected router: Router,
    private detailsController: DetailsController,
    private changesController: ChangesController,
    public route: ActivatedRoute) {

  }

  ngOnDestroy() {
    if(this.isAuthentificated() && !this.isSavedVoting()) {
      alert("Вы не сохранили результаты голосования!");
    }
  }

  @HostListener('window:beforeunload', ['$event'])
  beforeunloadHandler(event) {
    if (this.isAuthentificated()) {
      return this.isSavedVoting();
    }
    return true;
  }

  private isSavedVoting(): boolean {
    if (this.selectedItem.size > 0 && !this.voteInfo.voted) {
      this.notSaved = true;
      return false;
    }
    return true;
  }

  public prepareStatistic(item: VoteData): string {
    var result = '';
    var  votes: Array<VoterRecord> = this.voteStatistic.voters;
    //console.log(votes);
    if (votes) {
      result = '<ul>';
      votes.forEach( (voterRecord) => {
        result += "<li>" + voterRecord.voterId + ":" + voterRecord.voterRawMap[item.id] + ":" + voterRecord.voterPlaceMap[item.id] + "</li>"
      });
      result += '</ul>';
    }

    return result;
  }

  public placeFromUser(item: VoteData, voterRecord: VoterRecord): number {
    if (item && voterRecord && voterRecord.voterRawMap && voterRecord.voterRawMap[item.id]) {
      return voterRecord.voterRawMap[item.id];
    }
    return 0;
  }

  public placeForLeafsCount(item: VoteData, voterRecord: VoterRecord): any {
    if (this.isAllRecordsSelected(voterRecord)) {
      return "-";
    }
    if (voterRecord && voterRecord.voterPlaceMap && voterRecord.voterPlaceMap[item.id]) {
      return voterRecord.voterPlaceMap[item.id];
    }
    return 0;
  }
  private isAllRecordsSelected(voterRecord: VoterRecord): boolean {
    if (voterRecord && voterRecord.voterRawMap && this.voteStatistic) {
      return this.voteStatistic.allVoteItemIdList.length == Object.keys(voterRecord.voterRawMap).length;
    }
    return false;
  }


  public leafCount(item: VoteData, voterRecord: VoterRecord): number {
    var result = 1;

    if (this.isAllRecordsSelected(voterRecord)) {
      return (this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_CONCERT
                || this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_SHOW_HISTORY)
              ? (2*Object.keys(voterRecord.voterRawMap).length) : 2;
    }

    var place2 = this.placeForLeafsCount(item, voterRecord);
    if (place2 > 0) {
      result = (5 - place2);
    }

    return result;

  }

  public allLeafCount(item: VoteData): number {
    var result = 0;
    if (this.voteStatistic && this.voteStatistic.voters) {
       this.voteStatistic.voters.forEach( (voterRecord) => {
        result += this.leafCount(item, voterRecord);
      });
    }
    return result;
  }


  getVoteInfo(): void {
    if ( this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY ) {
      this.selectedItem = new Set<VoteData>();
      var voteInfoJson: string = localStorage.getItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
      if (voteInfoJson != null) {
        this.prepareView(JSON.parse(voteInfoJson));
      } else {
        this.voteService
          .getVoteItems(this.competitionShortInfo.compType)
          .then(voteInfo => this.saveInLocalStorageAndPrepare(voteInfo));
      }
    } else {
      this.voteService
                .getVoteItemsByCompId(this.competitionShortInfo.compId)
                .then(voteInfo => this.prepareView(voteInfo));

    }
  }

  isVotingStarted() {
    return this.startDate < this.currentDate;
  }

  isVotingEnded() {
    return this.endDate < this.currentDate;
  }

  public loadUserAvatar(userId: number): string {
    if (this.userAvatarMap.has(userId)) {
      return this.userAvatarMap.get(userId).previewImage;
    } else {
      this.loadUsersData(userId);
    }
  }
  public isLocalMp3Path(path: string): boolean {
    return path.indexOf("assets/mp3") != -1;
  }

  public loadUsername(userId: number): string {
    if (this.userAvatarMap.has(userId)) {
      return this.userAvatarMap.get(userId).username;
    } else {
      this.loadUsersData(userId);
    }
  }

  private loadUsersData(userId: number): void {
    this.currentUserData = new UserData();
    this.currentUserData.previewImage = DetailsController.defaultAvatar;
    this.detailsController.loadUserDetails(userId, this.currentUserData, this.changesController);
    this.userAvatarMap.set(userId, this.currentUserData);
  }

  public loadStatistic(): void {
    this.voteService
      .getVoteStatistic(this.competitionShortInfo.compId)
      .then(reply => this.voteStatistic = reply)
      .catch(ex => this.handleError(ex));
  }

  public getGoToLoginBtnTitle(): string {
    return (this.opinionsMode)
      ? "Для участия в обсуждении необходимо зайти на сайт"
      : "Для сохранения результатов голосования необходимо зайти на сайт";
  }

  public showRedirectToLoginBtn(): boolean {
    return !this.isAuthentificated()
            && (this.opinionsMode || (
            this.isVotingStarted()
            && !this.isVotingEnded()
            && ((this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY) || this.competitionShortInfo.adminMode)));
  }

  public showSaveVotingResultBtn(): boolean {
    return this.isAuthentificated()
            && !this.voteInfo.voted
            && !this.opinionsMode
            && this.isVotingStarted()
            && !this.isVotingEnded()
            && ((this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY)||this.competitionShortInfo.adminMode);
  }

  public showShowVotingResultsBtn(): boolean {
    return this.isAuthentificated()
            && !this.opinionsMode
            && !this.statisticMode
            && (this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT || this.competitionShortInfo.adminMode)
            && (this.voteInfo.voted || this.isVotingEnded());
  }

  public showVotingInstructionTtl(): boolean {
    return !this.opinionsMode
            && this.isVotingStarted()
            && !this.isVotingEnded()
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY;
  }

  public showConcertInstructionTtl(): boolean {
    return !this.opinionsMode
                && this.isVotingStarted()
                && this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_CONCERT;
  }

  public showToOpinionsTtl(): string {
    var unreadMsgs = localStorage.getItem(ChangesController.UNREAD_MESSAGES);
    if(unreadMsgs != null) {
      var jsonObject = JSON.parse(unreadMsgs);
      var keyId = (this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE ||
                          this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ ||
                          this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_FREE) ? 0 : this.competitionShortInfo.compId;
      if (jsonObject !== null && Object.keys(jsonObject).length > 0 && jsonObject.hasOwnProperty('' + keyId)) {
        return "Перейти к Oбсуждению (Новых сообщений: " + jsonObject['' + keyId] + ")";
      }
    }
    return "Перейти к Oбсуждению";
  }

  public showToOpinionsAction(): void {
    var unreadMsgs = localStorage.getItem(ChangesController.UNREAD_MESSAGES);
    if(unreadMsgs != null) {
          var keyId = (this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE ||
                              this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ ||
                              this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_FREE) ? 0 : this.competitionShortInfo.compId;
          var jsonObject = JSON.parse(unreadMsgs);
          if (jsonObject !== null && Object.keys(jsonObject).length > 0 && jsonObject.hasOwnProperty('' + keyId)) {
            delete jsonObject['' + keyId];
            localStorage.setItem(ChangesController.UNREAD_MESSAGES, JSON.stringify(jsonObject));
          }
        }
    this.opinionsMode = true;
  }

  public showVotingCloseTtl(): boolean {
    return !this.opinionsMode
            && this.isVotingEnded()
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY;
  }

  public showVotingWillStartTtl(): boolean {
    return !this.opinionsMode
            && !this.isVotingStarted()
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT;
  }

  public showConcertWillStartTtl(): boolean {
     return !this.opinionsMode
              && !this.isVotingStarted()
              && this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_CONCERT;
  }

  public showLikeHandBtn(voteItem: any): boolean {
    return !voteItem.order
            && !this.voteInfo.voted
            && (!this.isUserPartake(voteItem) || this.competitionShortInfo.adminMode)
            && this.isVotingStarted()
            && !this.isVotingEnded()
            && ((this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY) || this.competitionShortInfo.adminMode);
  }

  public showLikeNotAllowedBtn(voteItem: any): boolean {
    return this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_CONCERT
            && this.competitionShortInfo.compType !== CompetitionShortInfo.TYPE_SHOW_HISTORY
            && this.isUserPartake(voteItem);
  }


  public getVoteDataArray(): Array<VoteData> {
    return (this.opinionsMode) ? this.emptyVoteData : this.voteInfo.voteData;
  }

  private saveInLocalStorageAndPrepare(voteInfo: CompetitionInfo): void {
    localStorage.setItem(
      ChangesController.VOTING_PREFIX + voteInfo.competitionData.type,
      JSON.stringify(voteInfo));
    this.prepareView(voteInfo);
  }


  private prepareView(voteInfo: CompetitionInfo): void {
    this.voteInfo = voteInfo;
    this.competitionShortInfo.compId = this.voteInfo.competitionData.id;
    this.userItemId = null;
    this.findUserItemId();
    if (this.voteInfo.voted) {
      this.voteInfo.voteData.forEach(item => {
        if (item.order) this.selectedItem.add(item);
      });
      this.isAllSelected = this.checkIsAllSelected();
      this.sortItems();
    }
    this.startDate = new Date(this.voteInfo.competitionData.start);
    this.endDate = new Date(this.voteInfo.competitionData.end);
  }



  ngOnInit(): void {
    if (!this.authService.getAuth()) {
      this.authService.init().then(e => this.getVoteInfo());
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

  public isHistoryView(): boolean {
    return this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_SHOW_HISTORY;
  }

  public goToStatistic(): void {
    this.router.navigateByUrl("newstatistic");
  }


  protected sendResult(): void {
    this.notSaved = false;
    if (this.selectedItem.size > 0) {
      var selected: Array<VoteData> = JSON.parse(JSON.stringify(Array.from(this.selectedItem)));
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
        .then(reply => {
          localStorage.removeItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
          this.getVoteInfo();
        })
        .catch(e => this.handleError(e));
    } else {
      this.errorMsg = "Пожалуйста, выберите хотя бы одно исполнение!"
    }
  }

  anySelected(): boolean {
    return this.selectedItem.size > 0;
  }

  protected deleteVoting(): void {
    this.errorMsg = "";
    if (new Date(this.voteInfo.competitionData.end) < new Date()) {
      this.errorMsg = "Извините, голосование закрыто!";
      return;
    }
    if (!confirm("Ваше предыдущее голосование будет удалено! Продолжить?")) return;
    this.voteService
      .deleteVote(this.voteInfo.competitionData.id, this.authService.getAuth())
      .then(reply => {
        localStorage.removeItem(ChangesController.VOTING_PREFIX + this.competitionShortInfo.compType);
        this.getVoteInfo();
      })
      .catch(e => this.handleError(e));

  }


  onSelect(voteSelected: VoteData): void {
    this.notSaved = false;
    this.errorMsg = "";
    if (this.voteInfo.voted || (!this.competitionShortInfo.adminMode && this.isUserPartake(voteSelected))) {
      return;
    }
    if (new Date(this.voteInfo.competitionData.end) < new Date()) {
      this.errorMsg = "Извините, голосование закрыто!";
      return;
    }
    if (this.selectedItem.has(voteSelected)) {
      this.deselectItem(voteSelected);
    } else {
      this.selectItem(voteSelected);
    }
    this.isAllSelected = this.checkIsAllSelected();
    this.isSavedVoting();
  }

  showDetails(voteItem: VoteData): void {
    this.detailsmodal.showDetails(voteItem);
  }

  public isUserPartake(vote: VoteData): boolean {
    return (this.userItemId && vote.id === this.userItemId);
  }

  public getLastLetter(voteItem: VoteData): string {
    return (voteItem.usernames.length > 1) ? "ы" : "";
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


  public getBtnTitleGoToVoting(): string {
    if (this.competitionShortInfo.compType === CompetitionShortInfo.TYPE_CONCERT)  {
      return "Перейти к Записям";
    }
    return "Перейти к Голосованию";
  }


  public convertTimeToDate(time: any): string {
    var d = new Date(time);
    return d.getDate() + '.' + (d.getMonth() + 1) + '.' + d.getFullYear();
  }

  public goToLogin(): void {
    this.changesController.init();
    this.router.navigate(["/login"], {queryParams: {returnUrl: this.router.url}});
  }

  private checkIsAllSelected(): boolean {
    var result = true;
    this.voteInfo.voteData.forEach(item => {
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
      } else if (item.order && item.order > voteSelected.order) {
        item.order = item.order - 1;
      }
    });
    this.sortItems();
  }

  private selectItem(voteSelected: VoteData): void {
    this.voteInfo.voteData.forEach(item => {
      if (item.id === voteSelected.id) {
        item.order = (!this.competitionShortInfo.adminMode) ? this.selectedItem.size + 1 : 0;
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

  protected handleError(ex: any): void {
    if (ex.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт!");
      this.router.navigateByUrl("login");
    } else {
      this.errorMsg = (ex.json()) ? ex.json().message : ex.toString();
    }
  }

}
