import {Component, OnInit, ViewChild} from "@angular/core";
import {DiscussionItem} from "../model/DiscussionItem";
import {PartakeThread} from "../model/PartakeThread";
import {CompetitionComponent} from "../partaking/competition.component";
import {EditModalComponent} from "../modal/editmodal.component";
import {CompetitionMember} from "../model/CompetitionMember";
import {CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import {ChangesController} from "../changescontrol/changes.controller";

@Component({
  selector: 'discussion-thread-app',
  templateUrl: './discussion.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class DiscussionComponent extends CompetitionComponent implements OnInit {
  @ViewChild(EditModalComponent)
  editModal: EditModalComponent = new EditModalComponent(this.router);
  discussionItems: Array<DiscussionItem> = new Array<DiscussionItem>();
  newDiscussItem: DiscussionItem = new DiscussionItem();
  currentCompTypeMembers: Array<CompetitionMember>;


  ngOnInit(): void {
    //this.readMembers();
  }

  //readMembers(): void {
  //  var members: string = localStorage.getItem(ChangesController.COMPETITION_MEMBERS_PREFIX + this.competitionShortInfo.compType);
  //  if (members != null) {
  //    this.currentCompTypeMembers = JSON.parse(members);
  //  }
  //}

  public isAutheticated(): boolean {
    return (this.authService && this.authService.getAuth()) ? this.authService.getAuth().autheticated : false;
  }

  public isTakenPart(): boolean {
    if(this.isAutheticated() && this.currentCompTypeMembers) {
      return (this.currentCompTypeMembers.filter( (item) => {
        return item.mId === this.authService.getAuth().userId;
        }).length === 1) || this.discussionItems.length > 0;
    } else {
      return false;
    }
  }

  public addNewItem(): void {
    this.newDiscussItem = new DiscussionItem();
    this.newDiscussItem.competitionId = this.competitionShortInfo.compId;
    this.editModal.showModal("Новая заявка на участие",
          this.newDiscussItem,
          this.discussionItems,
          this.partakingService,
          "Ссылка на запись и описание:");
  }

  public reply(item: DiscussionItem): void {
    this.newDiscussItem = new DiscussionItem();
    this.newDiscussItem.competitionId = item.competitionId;
    this.newDiscussItem.parentMsgId = (item.parentMsgId == null)? item.msgId: item.parentMsgId;
    this.newDiscussItem.msgThreadId = item.msgThreadId;
    this.editModal.showModal("Ответ на сообщение пользователя " + item.authorId,
      this.newDiscussItem,
      this.discussionItems,
      this.partakingService,
      "Ваш комментарий:");
  }

  public editItem(item: DiscussionItem): void {
    this.editModal.showModal("Редактирование",
      item,
      null,
      this.partakingService,
      "Ссылка на запись и описание:");
  }

  public deleteItem(item: DiscussionItem): void {
    this.partakingService
      .deleteItem(item)
      .then(reply => this.removeItemFromList(item))
      .catch(e => this.handleError(e));
  }

  private removeItemFromList(item: DiscussionItem): void {
    this.discussionItems = this.discussionItems.filter((itm)=>{itm.msgId !==  item.msgId});
    super.reloadMembers();

  }

  public showThread(): void {
    this.loadDiscussion();
  }

  public goToLogin(): void {
    this.router.navigate(["/login"], { queryParams: { returnUrl: this.router.url }});
  }

  public canEdit(item: DiscussionItem): boolean {
    return this.isAutheticated() && this.isAmItemOwner(item);
  }

  public canReply(item: DiscussionItem): boolean {
    return this.isAutheticated() && !this.isAmItemOwner(item);
  }

  public canDelete(item: DiscussionItem): boolean {
    return this.isAutheticated() && this.isAmItemOwner(item)
      && (item.parentMsgId != null ||  this.discussionItems.length == 1);
  }

  public isAmItemOwner(item: DiscussionItem): boolean {
    return item.authorId === this.authService.getAuth().userId;
  }

  public isUpdated(item: DiscussionItem): boolean {
    return item.updateDate !== item.creationDate;
  }

  public getAvatar(item: DiscussionItem): string {
      return "../../assets/images/defaultAvatar.jpg"
  }



  private loadDiscussion(): void {
    this.partakingService
      .getPartakeDiscuss(this.competitionShortInfo.compId)
      .then(reply => this.handleThreadReply(reply))
      .catch(e => this.handleError(e));
  }



  private handleThreadReply(reply: PartakeThread): void {
    this.discussionItems = reply.discussionItems;
  }



}
