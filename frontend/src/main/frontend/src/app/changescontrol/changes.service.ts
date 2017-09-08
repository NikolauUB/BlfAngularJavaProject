import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ChangesKeywords} from "./ChangesKeywords";
import {ThreadChanges} from "./ThreadChanges";

@Injectable()
export class ChangesService {
  private keywordsUrl = 'api/getChangedKeywords';
  private threadUpdatesUrl = 'api/getThreadUpdates';

  constructor(private http: Http) {
  }

  public checkChanges(time:number): Promise<ChangesKeywords> {
    return this.http.get(this.keywordsUrl + "?ld=" + time)
      .toPromise()
      .then(response => response.json() as ChangesKeywords)
      .catch(this.handleError);
  }

  public getThreadUpdates(uTime:Date, thTime:Date, threadId: number): Promise<ThreadChanges> {
    return this.http.get(this.threadUpdatesUrl +
                          "?thid=" + threadId +
                          ((uTime != null)? "&uld=" + uTime:"") +
                          ((thTime != null) ? "&tld=" + thTime:""))
        .toPromise()
        .then(response => response.json() as ThreadChanges)
        .catch(this.handleError);
  }


  /**
   * Handles any rest errors
   * @param error
   * @returns {Promise<any>}
   */
  private handleError(error: any): Promise<any> {
    console.error('An error occurred in Auth service', error);
    return Promise.reject(error.message || error);
  }

}
