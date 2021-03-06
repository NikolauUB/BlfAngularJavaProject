import { Injectable }    from '@angular/core';
import {Headers, Http, RequestOptions} from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { PartakeThread } from '../model/PartakeThread';
import { DiscussionItem } from '../model/DiscussionItem';
import {ActiveCompetitions} from "app/model/ActiveCompetitions";
import {AuthService} from "../auth/auth.service";
import {CompetitionData} from "../model/CompetitionData";
import {EditInterface} from "../modal/edit.interface";
import {CompetitionMember} from "../model/CompetitionMember";

/**
 * This is common service for all partake components
 */
@Injectable()
export class PartakingService {
    //currentType: number;
    private postHeaders:Headers;
    private saveItemUrl = 'api/submitPartake';
    private deleteItemUrl = 'api/deletePartake';
    private deleteThemeUrl = 'api/deleteTheme';
    private partakeDiscussUrl = 'api/getPartakeDiscuss';
    private adminDiscussUrl = 'api/getPartakeDiscussForAdmin';
    private activeCompetitionsUrl = 'api/getActiveCompetitions';
    private futureCompetitionsUrl = 'api/getFutureCompetitions';
    private competitionDataUrl = 'api/getActiveCompetitionData';
    private futureCompetitionDataUrl = 'api/getFutureCompetitionData';
    private competitionsMembersUrl = 'api/getCompetitionMembers';


    constructor(private http: Http, private authService: AuthService) { }

    private setCSRFHeaders(token: string): void {
        this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
    }

    public getActiveCompetitions(): Promise<ActiveCompetitions> {
      return this.http.get(this.activeCompetitionsUrl)
        .toPromise()
        .then(response => response.json() as ActiveCompetitions)
        .catch(this.handleError);
    }

    public getFutureCompetitions(): Promise<ActiveCompetitions> {
      return this.http.get(this.futureCompetitionsUrl)
        .toPromise()
        .then(response => response.json() as ActiveCompetitions)
        .catch(this.handleError);
    }

    public getCompetitionData(type: number): Promise<CompetitionData> {
      return this.http
        .get(this.competitionDataUrl + "?tp=" + type)
        .toPromise()
        .then(response => response.json() as CompetitionData)
        .catch(this.handleError);
    }

    public getFutureCompetitionData(type: number): Promise<CompetitionData> {
      return this.http
        .get(this.futureCompetitionDataUrl + "?tp=" + type)
        .toPromise()
        .then(response => response.json() as CompetitionData)
        .catch(this.handleError);
    }

    public getCompetitionsMembers(): Promise<Array<CompetitionMember>> {
      return this.http
        .get(this.competitionsMembersUrl)
        .toPromise()
        .then(response => response.json() as Array<CompetitionMember>)
        .catch(this.handleError);
    }

    public getPartakeDiscuss(competitionId: number): Promise<PartakeThread> {
        return this.http.get(this.partakeDiscussUrl + "?cId=" + competitionId)
            .toPromise()
            .then(response => response.json() as PartakeThread)
            .catch(this.handleError);
    }

    public getAdminPartakeDiscuss(competitionId: number, themeId: number): Promise<PartakeThread> {
        return this.http.get(this.adminDiscussUrl + "?cId=" + competitionId + "&tId=" + themeId)
            .toPromise()
            .then(response => response.json() as PartakeThread)
            .catch(this.handleError);
    }

    public saveItem(discussionItem: DiscussionItem): Promise<DiscussionItem>  {
        this.setCSRFHeaders(this.authService.getAuth().tkn);
        return this.http
            .post(this.saveItemUrl,
            JSON.stringify(discussionItem),
            {headers: this.postHeaders})
            .toPromise()
            .then(response => response.json() as DiscussionItem)
            .catch(this.handleError);
    }

    public deleteItem(discussionItem: DiscussionItem): Promise<any>  {
      this.setCSRFHeaders(this.authService.getAuth().tkn);
      return this.http
        .delete(this.deleteItemUrl + "?iid=" + discussionItem.msgId,
          {headers: this.postHeaders})
        .toPromise()
        .catch(this.handleError);
    }

    public deleteTheme(themeId: number): Promise<any>  {
      this.setCSRFHeaders(this.authService.getAuth().tkn);
      return this.http
        .delete(this.deleteThemeUrl + "?thid=" + themeId,
          {headers: this.postHeaders})
        .toPromise()
        .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred in PartakingService service', error);
        return Promise.reject(error.message || error);
    }


}
