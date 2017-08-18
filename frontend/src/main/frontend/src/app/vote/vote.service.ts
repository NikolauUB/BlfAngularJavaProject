import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { VoteData } from '../model/VoteData';
import {CompetitionInfo} from "../model/CompetitionInfo";
import {AuthData} from "../model/auth/AuthData";

@Injectable()
export class VoteService {
  private postHeaders: Headers;
  private voteDataUrl = 'api/votedata?type=';
  private voteUrl = 'api/vote';

  constructor(private http: Http) { }

  private setCSRFHeaders(token: string): void {
    this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
  }

  getVoteItems(type): Promise<CompetitionInfo> {
    return this.http.get(this.voteDataUrl + type)
      .toPromise()
      .then(response => response.json() as CompetitionInfo)
      .catch(this.handleError);
  }

  vote(voteData: VoteData[], authData: AuthData): Promise<string>  {
    this.setCSRFHeaders(authData.token);
    return this.http
      .post(this.voteUrl,
        JSON.stringify(voteData),
        {headers: this.postHeaders})
      .toPromise()
      .then(response => response.url as string)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred in Vote service', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }
}
