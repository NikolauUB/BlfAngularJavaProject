import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import {AuthService} from "../../auth/auth.service";
import { VotingThread } from '../../model/VotingThread';
import { DiscussionItem } from '../../model/DiscussionItem';

@Injectable()
export class OpinionService {
    private postHeaders:Headers;
    private getVotingOpinionsUrl = 'api/getVotingOpinions?cId=';
    private saveOpinionsUrl = 'api/saveOpinion';
    private deleteOpinionUrl = 'api/deleteOpinion?iid=';


    constructor(private http: Http, private authService: AuthService) { }

    private setCSRFHeaders(token: string): void {
        this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
    }

    public getVotingOpinions(competitionId: number,
                             msgStartAfterId: number,
                             controlDate: number): Promise<VotingThread> {
        var additionalParams = "";
        additionalParams+= (msgStartAfterId !== null) ? "&saId=" + msgStartAfterId: "";
        additionalParams+= (controlDate !== null) ? "&cd=" + controlDate : "";
        return this.http
            .get(this.getVotingOpinionsUrl + competitionId + additionalParams)
            .toPromise()
            .then(response => response.json() as VotingThread)
            .catch(this.handleError);
    }

    public saveOpinionItem(discussionItem: DiscussionItem): Promise<DiscussionItem>  {
        this.setCSRFHeaders(this.authService.getAuth().tkn);
        return this.http
            .post(this.saveOpinionsUrl,
            JSON.stringify(discussionItem),
            {headers: this.postHeaders})
            .toPromise()
            .then(response => response.json() as DiscussionItem)
            .catch(this.handleError);
    }

    public deleteOpinionItem(discussionItem: DiscussionItem): Promise<any> {
        this.setCSRFHeaders(this.authService.getAuth().tkn);
        return this.http
            .delete(this.deleteOpinionUrl + discussionItem.msgId,
            {headers: this.postHeaders})
            .toPromise()
            .catch(this.handleError);

    }


    private handleError(error: any): Promise<any> {
        console.error('An error occurred in OpinionService service', error);
        return Promise.reject(error.message || error);
    }
}