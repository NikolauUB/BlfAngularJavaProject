import { Injectable }    from '@angular/core';
import {Headers, Http, RequestOptions} from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { PartakeThread } from '../model/PartakeThread';
import { DiscussionItem } from '../model/DiscussionItem';
import {AuthData} from "../model/auth/AuthData";
//!import {ChangesRequest} from "../model/ChangesRequest";
//!import {PartakeThreadChanges} from "../model/PartakeThreadChanges";
import {StatusOfDiscussionItem} from "../model/StatusOfDiscussionItem";
import {ActiveCompetitions} from "app/model/ActiveCompetitions";


@Injectable()
export class PartakingService {
    private postHeaders:Headers;
    private saveItemUrl = 'api/submitPartake';
    private deleteItemUrl = 'api/deletePartake';
    private partakeDiscussUrl = 'api/getPartakeDiscuss';
    //!private partakeChangesUrl = 'api/getPartakeChanges';
    private activeCompetitionsUrl = 'api/getActiveCompetitions';


    constructor(private http: Http) { }

    private setCSRFHeaders(token: string): void {
        this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
    }
    //todo future improvement
/*
    getPartakeChanges(changesRequest: ChangesRequest): Promise<PartakeThreadChanges> {
        let params = new URLSearchParams();
        params.set('time', '' + changesRequest.controlDate.getTime());
        params.set('threadId', '' + changesRequest.threadId);
        let options = new RequestOptions({
          search: params
        });
        return this.http.get(this.partakeChangesUrl, options)
          .toPromise()
          .then(response => response.json() as PartakeThreadChanges)
          .catch(this.handleError);
    }
*/
    //remove partakeThreadChanges.deletedIds before
  /*
    getPartakeDiscussChangedItems(partakeThreadChanges: PartakeThreadChanges, authData: AuthData): Promise<PartakeThread> {
        this.setCSRFHeaders(authData.token);
        return this.http.
          post(this.partakeDiscussUrl,
          JSON.stringify(partakeThreadChanges),
          {headers: this.postHeaders})
          .toPromise()
          .then(response => response.json() as PartakeThread)
          .catch(this.handleError);
    }
*/

    getActiveCompetitions(): Promise<ActiveCompetitions> {
      return this.http.get(this.activeCompetitionsUrl)
        .toPromise()
        .then(response => response.json() as ActiveCompetitions)
        .catch(this.handleError);
    }

    getPartakeDiscuss(competitionId: number): Promise<PartakeThread> {
        return this.http.get(this.partakeDiscussUrl + "?cId=" + competitionId)
            .toPromise()
            .then(response => response.json() as PartakeThread)
            .catch(this.handleError);
    }

    saveItem(discussionItem: DiscussionItem, authData: AuthData): Promise<StatusOfDiscussionItem>  {
        this.setCSRFHeaders(authData.token);
        return this.http
            .post(this.saveItemUrl,
            JSON.stringify(discussionItem),
            {headers: this.postHeaders})
            .toPromise()
            .then(response => response.json() as StatusOfDiscussionItem)
            .catch(this.handleError);
    }
    deleteItem(discussionItem: DiscussionItem, authData: AuthData): Promise<StatusOfDiscussionItem>  {
      this.setCSRFHeaders(authData.token);
      return this.http
        .delete(this.deleteItemUrl + "?iid=" + discussionItem.msgId,
          {headers: this.postHeaders})
        .toPromise()
        .then(response => response.json() as StatusOfDiscussionItem)
        .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred in PartakingService service', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }


}
