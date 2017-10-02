import {Component, AfterViewInit, Inject, ChangeDetectorRef} from "@angular/core";
import {DiscussionItem} from "../../model/DiscussionItem";
import {VotingThread} from "../../model/VotingThread";
import { OpinionService } from "./opinion.service";
import  {AuthService } from "../../auth/auth.service";
import { Router } from '@angular/router';
import { CompetitionShortInfo} from "../../partaking/CompetitionShortInfo";
import {ChangesController} from "../../changescontrol/changes.controller";
import {DetailsController} from "../../auth/userdetails/details.controller";

declare var nicEditor: any;

@Component({
    selector: 'opinions-app',
    templateUrl: './opinions.component.html',
    styleUrls: [ '../voting.component.css' ]
})
export class OpinionsComponent implements AfterViewInit {
    nicEdit: any;
    nicEditE: any;
    newOpinionItem: DiscussionItem = new DiscussionItem();
    editItemId: number = -1;
    votingThread: VotingThread = new VotingThread();
    browserCanWorkWithIndexedDB: boolean = false;
    idOfFirstPageItem: number = null;


    opinionErrorMsg: string;
    constructor(private opinionService: OpinionService,
                private authService: AuthService,
                private router: Router,
                private userDetailsController: DetailsController,
                private changesController: ChangesController,
                private competitionShortInfo: CompetitionShortInfo,
                @Inject(ChangeDetectorRef) private changeDetectorRef: ChangeDetectorRef) {

    }

    public isAutheticated(): boolean {

        return (this.authService && this.authService.getAuth()) ? this.authService.getAuth().auth : false;
    }

    public viewPreviousPage(): void {
        this.opinionService
            .getVotingOpinions(this.competitionShortInfo.compId, this.idOfFirstPageItem, null)
            .then(reply => this.sortThread(reply))
            .catch(e => this.handleError(e));


    }

    public hasPrevious(): boolean {
        if (this.votingThread.yc === -1) {
           return  ((this.votingThread.ac - 10) > 0);
        } else {
            return (this.votingThread.yc > 0);
        }
    }

    public hasNext(): boolean {
        return (this.votingThread.yc !== -1);
    }

    public getOpinionCount(): string {
        if (this.votingThread.yc === -1) {
            return  " (количество: " + (this.votingThread.ac - 10) + ")";
        } else {
            return  " (количество: " + this.votingThread.yc + ")";
        }
    }

    ngAfterViewInit() {
        this.nicEdit = new nicEditor({
            buttonList: ['bold', 'italic', 'underline', 'left', 'center', 'right', 'justify',
                'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat',
                'indent', 'outdent', 'hr', 'image', 'forecolor', 'bgcolor', 'link', 'unlink',
                'fontSize', 'fontFamily', 'fontFormat', 'xhtml']
        }).panelInstance('nickEdit');
        this.loadOpinionsFirstPage();

    }

    public loadOpinionsFirstPage(): void {
        this.opinionService
            .getVotingOpinions(this.competitionShortInfo.compId, null, null)
            .then(reply => this.sortThread(reply))
            .catch(e => this.handleError(e));
    }

    private sortThread(reply: VotingThread) {
        this.votingThread = reply;
        if (this.votingThread.oi.length > 0) {
            this.votingThread.oi.sort((i1, i2) => {
               if (i1.msgId > i2.msgId) {
                   return 1;
               }
               if (i1.msgId < i2.msgId) {
                   return -1;
               }
               return 0;
            });
            this.votingThread.oi.forEach((item, i) => {
                if (i === 0) {
                    this.idOfFirstPageItem = item.msgId;
                }
                this.getAuthorDetails(item);
            });
        }
    }
    public deleteItem(item: DiscussionItem): void {
        if (this.isAutheticated()) {
            this.opinionService.deleteOpinionItem(item)
                .then(e => this.removeFromList(item))
                .catch(e =>this.handleError(e));
        }
    }

    private removeFromList(item: DiscussionItem): void {
    
    }

    public createItem(item: DiscussionItem): void {
        if (this.isAutheticated()) {
            item.authorId = this.authService.getAuth().uId;
            item.competitionId = this.competitionShortInfo.compId;
            this.saveItem(item, this.nicEdit.instanceById('nickEdit').getContent());
        }
    }
    public isItemEditting(item: DiscussionItem): boolean {
        return (this.editItemId !== -1) && (this.editItemId === item.msgId);
    }

    public editItem(item: DiscussionItem): void {
        this.editItemId = item.msgId;
        this.changeDetectorRef.detectChanges();
        this.nicEditE = new nicEditor({
            buttonList: ['bold', 'italic', 'underline', 'left', 'center', 'right', 'justify',
                'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat',
                'indent', 'outdent', 'hr', 'image', 'forecolor', 'bgcolor', 'link', 'unlink',
                'fontSize', 'fontFamily', 'fontFormat', 'xhtml']
        }).panelInstance('nickEditE');
        this.nicEditE.instanceById('nickEditE').setContent(item.msgText);
    }

    public updateItem(item: DiscussionItem): void {
        this.saveItem(item, this.nicEditE.instanceById('nickEditE').getContent());
        this.editItemId = -1;
    }

    private saveItem(item: DiscussionItem, msg: string): void {
        if (this.isAutheticated() && item.msgText != msg) {
            item.msgText = msg;
            this.opinionService
                .saveOpinionItem(item)
                .then(reply => this.treatResult(reply, item))
                .catch(e => this.handleError(e));
        }
    }

    private treatResult(replyItem: DiscussionItem, item: DiscussionItem): void {
        if (item.msgId == null) {
            this.loadOpinionsFirstPage();
        } else {
            item = replyItem;
        }

    }

    public cancelChanges(item: DiscussionItem): void {
        var msg = this.nicEdit.instanceById('nickEdit').getContent();
        if (item.msgText != msg) {
            this.nicEdit.instanceById('nickEdit').setContent((item.msgText) ? item.msgText : "");
        }
    }


    public convertTimeToDate(time: any): string {
        var d = new Date(time);
        return d.getDate() + '.' + (d.getMonth()+1) + '.' + d.getFullYear() + ', ' + d.getHours() + ':' + d.getMinutes();
    }

    public canEdit(item: DiscussionItem): boolean {
        return this.isAutheticated() && (item.authorId === this.authService.getAuth().uId);
    }

    public canReply(item: DiscussionItem): boolean {
        return this.isAutheticated() && (item.authorId !== this.authService.getAuth().uId);
    }

    public canDelete(item: DiscussionItem): boolean {
        return this.isAutheticated() && (item.authorId === this.authService.getAuth().uId);
    }

    public isUpdated(item: DiscussionItem): boolean {
        return item.updateDate !== item.creationDate;
    }


    protected handleError(e: any) : void {
        if(e.status === 403) {
            alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт!");
            this.router.navigateByUrl("login");
        } else {
            this.opinionErrorMsg = e.json().message || e.toString();
        }
    }

    private getAuthorDetails(item: DiscussionItem): void {
        item.authorUsername = "Пользователь " + item.authorId;
        item.authorAvatar = DetailsController.defaultAvatar;
        //if (this.browserCanWorkWithIndexedDB) {
        //    this.userDetailsController.loadUserDetails(item);
        //} else {
        //    this.userDetailsController.loadUserDetailFromDBForDiscuss(item);
        //}
    }

}