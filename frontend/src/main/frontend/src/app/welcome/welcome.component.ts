import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'welcome-app',
    templateUrl: './welcome.component.html',
    styleUrls: [ '../vote/voting.component.css' ]
})
export class WelcomeComponent implements OnInit {
    ngOnInit(): void {}
}