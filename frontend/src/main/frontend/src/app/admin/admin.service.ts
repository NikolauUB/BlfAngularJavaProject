import { Injectable }    from '@angular/core';
import {Headers, Http, RequestOptions} from '@angular/http';
import 'rxjs/add/operator/toPromise';
import {CompetitionItem} from "../model/CompetitionItem";
import {AuthService} from "../auth/auth.service";



@Injectable()
export class AdminService {
    private postHeaders:Headers;
    private saveCompetitionItemUrl = 'api/saveCompetitionItem';
    private loadCompetitionItemUrl = 'api/loadCompetitionItem';

    constructor(private http: Http, private authService: AuthService) { }

    private setCSRFHeaders(token: string): void {
        this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
    }

    public saveItem(competitionItem: CompetitionItem): Promise<CompetitionItem>  {
        this.setCSRFHeaders(this.authService.getAuth().token);
        return this.http
            .post(this.saveCompetitionItemUrl,
            JSON.stringify(competitionItem),
            {headers: this.postHeaders})
            .toPromise()
            .then(response => response.json() as CompetitionItem)
            .catch(this.handleError);
    }

    public loadItem(competitionItem: CompetitionItem): Promise<CompetitionItem>  {
        this.setCSRFHeaders(this.authService.getAuth().token);
        return this.http
            .post(this.loadCompetitionItemUrl,
            JSON.stringify(competitionItem),
            {headers: this.postHeaders})
            .toPromise()
            .then(response => response.json() as CompetitionItem)
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred in PartakingService service', error);
        return Promise.reject(error.message || error);
    }
}