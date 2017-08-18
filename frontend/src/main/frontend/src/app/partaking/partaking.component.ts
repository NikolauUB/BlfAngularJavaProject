import {Component, OnInit, ViewChild} from "@angular/core";
import {AppModalComponent} from "../modal/appmodal.component";
import {AuthData} from "../model/auth/AuthData";
import {PartakingService} from "./partaking.service";
import {AuthService} from "../auth/auth.service";
import {Router} from "@angular/router";
import {DiscussionItem} from "../model/DiscussionItem";
import {StatusOfDiscussionItem} from "../model/StatusOfDiscussionItem";
import {PartakeThread} from "../model/PartakeThread";
import {ActiveCompetitions} from "../model/ActiveCompetitions";
import {CompetitionData} from "../model/CompetitionData";

@Component({
  selector: 'partaking-app',
  templateUrl: './partake.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class PartakingComponent implements OnInit {
  @ViewChild(AppModalComponent)
  modal: AppModalComponent = new AppModalComponent();
  newDiscussItem: DiscussionItem = new DiscussionItem();
  activeCompetitions: ActiveCompetitions;
  currentCompetition: CompetitionData;
  threadMap: Map<CompetitionData,DiscussionItem[]> = new Map<CompetitionData,DiscussionItem[]>();
  errorMsg: string;
  reply: boolean = false;
  edit: boolean = false;
  authInfo: AuthData;

  constructor(
    private partakingService: PartakingService,
    private authService: AuthService,
    private router: Router) {
    this.authInfo = this.authService.getAuth();
  }


  ngOnInit(): void {
    this.authInfo = this.authService.getAuth();
    this.loadActiveCompetitions()
    this.refreshAddEditStatus();
  }

  loadActiveCompetitions(): void {
    this.partakingService
      .getActiveCompetitions()
      .then( reply => this.handleCompetitionReply(reply))
  }

  loadDiscussion(competition: CompetitionData): void {
    this.partakingService
      .getPartakeDiscuss(competition.id)
      .then(reply => this.handleThreadReply(reply, competition))
  }
  saveDiscussItem(): void {
    this.newDiscussItem.competitionId = this.currentCompetition.id;
    this.partakingService
      .saveItem(this.newDiscussItem, this.authInfo)
      .then(reply => this.handleSaveReply(reply));

  }

  deleteMsg(item: DiscussionItem, competition: CompetitionData): void {
    this.partakingService
      .deleteItem(item, this.authInfo)
      .then(reply => this.handleRemoveReply(reply, item, competition));
  }



  handleCompetitionReply(reply: any): void {
    this.activeCompetitions = reply;
    if(this.authInfo.autheticated) {
      for (let competition of this.activeCompetitions.activeList) {
        this.loadDiscussion(competition);
      }
    }
  }

  handleThreadReply(reply: any, competition: CompetitionData): void {
    let discuss: PartakeThread;
    discuss = reply;
    if (discuss.code !== 200) {
      this.errorMsg = discuss.errorMsg;
    } else {
      this.errorMsg = "";
      if (discuss.discussionItems === null) {
        let itemArray: Array<DiscussionItem> = new Array<DiscussionItem>();
        this.threadMap.set(competition, itemArray);
      } else {
        this.threadMap.set(competition, discuss.discussionItems);
      }
    }
  }

  private handleSaveReply(reply: any): void {
    let saveStatus: StatusOfDiscussionItem = reply;
    if (saveStatus.code !== 200) {
      this.errorMsg = saveStatus.errorMsg;
    } else {
      this.errorMsg = "";
      if (this.edit) {
        this.newDiscussItem.updateDate = saveStatus.item.updateDate;
      } else {
        if (this.threadMap.has(this.currentCompetition)) {
          this.threadMap.get(this.currentCompetition).push(saveStatus.item);
        } else {
          let itemArray: Array<DiscussionItem> = new Array<DiscussionItem>();
          itemArray.push(saveStatus.item);
          this.threadMap.set(this.currentCompetition, itemArray);
        }
      }
      this.refreshAddEditStatus();
    }
  }

  handleRemoveReply(reply: any, item: DiscussionItem, competition: CompetitionData): void {
    let deleteStatus: StatusOfDiscussionItem = reply;
    if (deleteStatus.code !== 200) {
      this.errorMsg = deleteStatus.errorMsg;
    } else {
      this.errorMsg = "";
      let index: number = this.threadMap.get(competition).indexOf(item);
      if(index !== -1) {
        this.threadMap.get(competition).splice(index, 1);
      }
    }
  }
  public isCompetitionsHasPartakeRequest(competition: CompetitionData): boolean {
      if(this.threadMap.has(competition)) {
        return this.threadMap.get(competition).length > 0
      }
      return false;
  }

  public canDelete(item: DiscussionItem, competition: CompetitionData): boolean {
      if (item.authorId !== this.authInfo.userId) {
        return false;
      } else if (item.parentMsgId === null && this.threadMap.get(competition).length > 1) {
        return false;
      }
      return true;
  }

  public canEdit(item: DiscussionItem): boolean {
    return item.authorId === this.authInfo.userId;
  }

  public refreshAddEditStatus(): void {
    this.newDiscussItem = new DiscussionItem();
    this.currentCompetition = null;
    this.reply = false;
    this.edit = false;
    this.errorMsg = "";
  }

  public showReplyBox(parentMsg:DiscussionItem, competition: CompetitionData): void {
    this.currentCompetition = competition;
    this.newDiscussItem.msgThreadId = parentMsg.msgThreadId;
    if(parentMsg.parentMsgId == null) {
      this.newDiscussItem.parentMsgId = parentMsg.msgId;
    } else {
      this.newDiscussItem.parentMsgId = parentMsg.parentMsgId;
    }
    this.newDiscussItem.msgText = "";
    this.reply = true;
  }

  public showEditBox(msg:DiscussionItem, competition: CompetitionData): void {
    this.currentCompetition = competition;
    this.newDiscussItem = msg;
    this.edit = true;
  }

  public addPartakeRequest(competition: CompetitionData): void {
    if (this.authInfo.autheticated) {
      this.currentCompetition = competition;
      this.reply = true;
    } else {
      this.errorMsg = "Please login or register first";
    }
  }



}
