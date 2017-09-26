import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {DiscussionItem} from "../model/DiscussionItem";
import {PartakeThread} from "../model/PartakeThread";
import {CompetitionComponent} from "../partaking/competition.component";
import {EditModalComponent} from "../modal/editmodal.component";
import {CompetitionMember} from "../model/CompetitionMember";
import {CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import {ChangesController} from "../changescontrol/changes.controller";
import {EditInterface} from "../modal/edit.interface";
import {DetailsController} from "../auth/userdetails/details.controller";
import {ThreadChanges} from "../changescontrol/ThreadChanges";


@Component({
  selector: 'discussion-thread-app',
  templateUrl: './discussion.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class DiscussionComponent extends CompetitionComponent implements OnInit, EditInterface, AfterViewInit {
  @ViewChild(EditModalComponent)
  editModal: EditModalComponent = new EditModalComponent(this.router);
  discussionItems: Array<DiscussionItem> = new Array<DiscussionItem>();
  newDiscussItem: DiscussionItem = new DiscussionItem();



  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    var hideDiscuss = localStorage.getItem(ChangesController.HIDE_PARTAKE_DISCUSS_PREFIX + this.competitionShortInfo.compType);
    if (hideDiscuss != null && hideDiscuss == "0") {
      this.showDiscuss();
    }
  }

  private showDiscuss(): void {
    if (this.isTakenPart()) {
      if (this.competitionShortInfo.userThread > 0) {
          this.themeController
              .createStoreAndLoadMaxUpdated(this.competitionShortInfo.userThread)
              .then(thTime => this.checkChangesInThread(thTime));
      }
    }
  }

  private checkChangesInThread(thTime: Date) {
      this.changesController
          .checkChangesInThread(thTime, this.competitionShortInfo.userThread)
          .then(reply => this.updateThemeDetails(reply));
  }


  private updateThemeDetails(threadChanges: ThreadChanges) {
    this.themeController
        .loadThemeById(this.competitionShortInfo.userThread)
        .then(res => {
          if (res == null || threadChanges.thChanged) {
            this.loadDiscussion();
          } else {
            this.discussionItems = res;
            this.discussionItems.forEach((item)=> {
              this.getAuthorDetails(item);
            });
          }
        });
  }

  public goToLogin(): void {
    this.router.navigate(["/login"], { queryParams: { returnUrl: this.router.url }});
  }


  private getAuthorDetails(item: DiscussionItem): void {
    item.authorUsername = "Пользователь " + item.authorId;
    item.authorAvatar = DetailsController.defaultAvatar;
    this.userDetailsController.loadUserDetails(item);
  }


  //************************************
  //check permission section
  //************************************
  public isAdmin(): boolean {
    return this.isAutheticated() && this.authService.getAuth().uName === "NikolayUB";
  }

  public isAutheticated(): boolean {
    return (this.authService && this.authService.getAuth()) ? this.authService.getAuth().auth : false;
  }

  public hasOtherRestrictions(): boolean {
    return this.competitionShortInfo.isUserChooseAlternativeProgramm();
  }

  public isTakenPart(): boolean {
    return (this.isAutheticated() && this.competitionShortInfo.userThread != -1)
  }

  public canEdit(item: DiscussionItem): boolean {
    return this.isAutheticated() && this.isAmItemOwner(item);
  }

  public canReply(item: DiscussionItem): boolean {
    return this.isAutheticated() && !this.isAmItemOwner(item);
  }

  public canDelete(item: DiscussionItem): boolean {
    return this.isAutheticated() && this.isAmItemOwner(item);
  }

  //*****************************
  // check state of item
  //*****************************
  private isAmItemOwner(item: DiscussionItem): boolean {
    return item.authorId === this.authService.getAuth().uId;
  }

  public isUpdated(item: DiscussionItem): boolean {
    return item.updateDate !== item.creationDate;
  }


  //****************************
  // add, edit item section
  //****************************
  /**
   * add root item
   */
  public addNewItem(): void {
    if(!this.isAutheticated()) return;
    this.newDiscussItem = new DiscussionItem();
    this.newDiscussItem.competitionId = this.competitionShortInfo.compId;
    this.editModal.showModal("Новая заявка на участие",
          this.newDiscussItem,
          this,
          "Ссылка на запись и описание:");
  }

  /**
   * Reply to other item
   * @param {DiscussionItem} item
   */
  public reply(item: DiscussionItem): void {
    if(!this.canReply(item)) return;
    this.newDiscussItem = new DiscussionItem();
    this.newDiscussItem.competitionId = item.competitionId;
    this.newDiscussItem.parentMsgId = (item.parentMsgId == null)? item.msgId: item.parentMsgId;
    this.newDiscussItem.msgThreadId = item.msgThreadId;
    this.editModal.showModal("Ответ на сообщение пользователя " + item.authorUsername,
      this.newDiscussItem,
      this,
      "Ваш комментарий:");
  }

  /**
   * Edit existing item
   * @param {DiscussionItem} item
   */
  public editItem(item: DiscussionItem): void {
    if(!this.canEdit(item)) return;
    this.editModal.showModal("Редактирование",
      item,
      this,
      "Ссылка на запись и описание:");
  }


  /**
   * This method is called by edit modal on submit changes
   * @param {DiscussionItem} discussionItem
   */
 saveItem(discussionItem: DiscussionItem): void {
    var isNew =  (discussionItem.msgId == null);
    discussionItem.authorId = this.authService.getAuth().uId;
    this.partakingService
      .saveItem(discussionItem)
      .then(reply => this.handleSaveReply(reply, isNew))
      .catch(e => this.editModal.handleError(e));

  }

  private handleSaveReply(reply: DiscussionItem, isNew: boolean) {
    if (isNew) {
      this.newDiscussItem = reply;
      if (this.discussionItems.length === 0) {
        //if first message then reload member list
        super.reloadMembers();
      }
      this.discussionItems.push(this.newDiscussItem);
      this.getAuthorDetails(this.newDiscussItem);
    } else {
      //update updateDate
      var discItem = this.discussionItems
        .filter((itm)=>{
          return itm.msgId ===  reply.msgId;
      });
      if(discItem.length == 1) {
        discItem[0].updateDate = reply.updateDate;
      }
      //this.loadDiscussion();
    }
    this.editModal.hide();
  }

  //*********************
  // delete item section
  //*********************
  public deleteItem(item: DiscussionItem): void {
    if(!this.canDelete(item)) return;
    if (item.parentMsgId == null && confirm("Вы уверены, что хотите удалить заявку?")) {
      this.partakingService
        .deleteTheme(item.msgThreadId)
        .then(reply => this.removeDiscussion())
        .catch(e => this.handleError(e));
    } else {
      this.partakingService
        .deleteItem(item)
        .then(reply => this.removeItemFromList(item))
        .catch(e => this.handleError(e));
    }
  }
  private removeDiscussion(): void {
    this.discussionItems = new Array<DiscussionItem>();
    //to reload member list
    super.reloadMembers();
  }

  private removeItemFromList(item: DiscussionItem): void {
    this.discussionItems = this.discussionItems.filter((itm)=>{
      return itm.msgId !== item.msgId;
    });
    if(this.discussionItems.length === 0) {
      super.reloadMembers();
    }
    //this.loadDiscussion();
  }


  //*************************
  // load discussion section
  //*************************
  public hideThread(): void {
    this.discussionItems = new Array<DiscussionItem>();
    localStorage.setItem(ChangesController.HIDE_PARTAKE_DISCUSS_PREFIX + this.competitionShortInfo.compType, "1");
  }
  public showThread(): void {
    this.showDiscuss();
    localStorage.setItem(ChangesController.HIDE_PARTAKE_DISCUSS_PREFIX + this.competitionShortInfo.compType, "0");
  }

  public showAdminThread(): void {
      if(this.competitionShortInfo.adminModeUserThread !== -1) {
          this.loadDiscussionForAdmin();
      }
  }

  private loadDiscussionForAdmin(): void {
    this.partakingService
        .getAdminPartakeDiscuss(this.competitionShortInfo.compId, this.competitionShortInfo.adminModeUserThread)
        .then(reply => this.handleLoadDiscussionReply(reply))
        .catch(e => this.handleError(e));
  }

  private loadDiscussion(): void {
    this.partakingService
      .getPartakeDiscuss(this.competitionShortInfo.compId)
      .then(reply => this.handleLoadDiscussionReply(reply))
      .catch(e => this.handleError(e));
  }

  private handleLoadDiscussionReply(reply: PartakeThread): void {
    this.discussionItems = reply.discussionItems;
    if (!this.isAdmin()) { //no cache for admin
      this.themeController.cleanTheme(reply.threadId);
      this.themeController.saveThemeInDBbyId(this.discussionItems, reply.threadId, reply.thUpdated);
    }
    this.discussionItems.forEach((item)=>{
      this.getAuthorDetails(item);
    });


  }
}
