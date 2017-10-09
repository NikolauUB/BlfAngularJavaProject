import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'welcome-app',
    templateUrl: './welcome.component.html',
    styleUrls: [ '../vote/voting.component.css' ]
})
export class WelcomeComponent implements OnInit {
    //todo set correct dates here
    jsonOb:any = "{\"start\":1508187594796,\"end\":1510865994796}";
    jsonFree:any = "{\"start\":1509397194796,\"end\":1512075594796}";
    dataOb:any = JSON.parse(this.jsonOb);
    obStartDate: Date = new Date(this.dataOb.start);
    obEndDate: Date = new Date(this.dataOb.end);
    dataFree:any = JSON.parse(this.jsonFree);
    freeStartDate: Date = new Date(this.dataFree.start);
    freeEndDate: Date = new Date(this.dataFree.end);
    currentDate: Date = new Date();

    ngOnInit(): void {
        this.currentDate = new Date();
    }

    isObStarted(): boolean {
        return this.obStartDate < this.currentDate;
    }

    isObEnded(): boolean {
        return this.obEndDate < this.currentDate;
    }

    isFreeStarted(): boolean {
        return this.freeStartDate < this.currentDate;
    }

    isFreeEnded(): boolean {
        return this.freeEndDate < this.currentDate;
    }

}