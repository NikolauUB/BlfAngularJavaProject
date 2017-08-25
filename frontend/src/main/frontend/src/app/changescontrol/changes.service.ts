import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ChangesKeywords} from "./ChangesKeywords";

@Injectable()
export class ChangesService {
  private keywordsUrl = 'api/getChangedKeywords';

  constructor(private http: Http) {
  }

  public checkChanges(time:number): Promise<ChangesKeywords> {
    return this.http.get(this.keywordsUrl + "?ld=" + time)
      .toPromise()
      .then(response => response.json() as ChangesKeywords)
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
