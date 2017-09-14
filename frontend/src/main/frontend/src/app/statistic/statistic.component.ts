import {Component, ViewChild} from "@angular/core";
import {ShowpictureComponent} from "../modal/showpicture.component";

@Component({
    selector: 'statistic-app',
    templateUrl: './statistic.component.html',
    styleUrls: [ '../vote/voting.component.css' ]
})
export class StatisticComponent {
    @ViewChild(ShowpictureComponent)
    modal:ShowpictureComponent = new ShowpictureComponent();

    public showVenok(path:string):void {
        this.modal.showPicture(path);
    }
}