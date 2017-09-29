import { Component, OnInit } from "@angular/core";
import {DiscussionComponent} from "../discussion/discussion.component";
import { CompetitionShortInfo} from "../partaking/CompetitionShortInfo";
import { CompetitionMember} from "../model/CompetitionMember";
import {CompetitionItem} from "../model/CompetitionItem";
import {AdminService} from "./admin.service";
import {PartakingService} from "../partaking/partaking.service";
import {Router} from "@angular/router";
import {ChangesController} from "../changescontrol/changes.controller";
import {ThemeController} from "../theme/theme.controller";
import {AuthService} from "../auth/auth.service";
import {DetailsController} from "../auth/userdetails/details.controller";

@Component({
    selector: 'admin-app',
    templateUrl: './admin.component.html',
    styleUrls: [ '../vote/voting.component.css' ]
})
export class AdminComponent extends DiscussionComponent implements OnInit   {
    selectedItem: CompetitionMember;
    item: CompetitionItem = new CompetitionItem();
    errorMsg: string;

    constructor(private adminService: AdminService,
                protected partakingService: PartakingService,
                protected authService: AuthService,
                protected router: Router,
                protected competitionShortInfo: CompetitionShortInfo,
                protected userDetailsController: DetailsController,
                protected changesController: ChangesController,
                protected themeController: ThemeController) {
        super(partakingService, authService, router, competitionShortInfo,
            userDetailsController, changesController,themeController);
    }

    ngOnInit(): void {
        (!this.authService.getAuth()) ? this.authService.init().then(e=>this.checkAccess()) :this.checkAccess();
    }

    private checkAccess(): void {
        if(!this.isAdmin()) {
            this.router.navigateByUrl('error403');
        } else {
            this.reloadMembers();
        }
    }

    onSelect(member: CompetitionMember): void {
        this.selectedItem = member;
        this.competitionShortInfo.adminModeUserThread = member.threadId;
        this.loadItem(member);
    }

    private loadItem(member: CompetitionMember): void {
        this.errorMsg = null;
        //load by mId and compId
        //else
        this.item = new CompetitionItem();
        this.item.userId = member.mId;
        this.item.compId = this.competitionShortInfo.compId;
        this.adminService
            .loadItem(this.item)
            .then(reply => this.item = reply)
            .catch(e => this.handleError(e));
    }

    saveCompItem(): void {
        this.errorMsg = null;
        this.adminService
            .saveItem(this.item)
            .then(reply => this.item = reply)
            .catch(e => this.handleError(e));
    }

    deleteCompItem(): void {
        this.errorMsg = null;
        this.adminService
            .removeItem(this.item)
            .catch(e => this.handleError(e));
    }

    isMemberSelected(member:CompetitionMember): boolean {
        return this.selectedItem == member;
    }
}