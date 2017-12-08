import { Injectable }    from '@angular/core';
import {Headers, Http, RequestOptions} from '@angular/http';
import {AuthService} from "../auth/auth.service";
import { UserStatisticHistory } from '../model/votestatistic/UserStatisticHistory';

@Injectable()
export class NewstatisticService {
  private allStatisticUrl = 'api/allstatistic';

  constructor(private http: Http) { }

  public getAllStatistic(): Promise<Array<UserStatisticHistory>> {
    return this.http.get(this.allStatisticUrl)
          .toPromise()
          .then(response => response.json() as Array<UserStatisticHistory>)
          .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred in NewstatisticService service', error);
    return Promise.reject(error.message || error);
  }
}
