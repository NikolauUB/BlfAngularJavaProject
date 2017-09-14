import {Component, ViewChild} from "@angular/core";
import { ModalComponent } from "./modal.component";

@Component({
    selector: 'picture-modal-component',
    templateUrl: './picture.component.html'

})
export class ShowpictureComponent {
    @ViewChild(ModalComponent)
    modal:ModalComponent = new ModalComponent();
    pictureUrl: string;

    public showPicture(url:string) {
        this.pictureUrl = url;
        this.modal.show();
    }
}