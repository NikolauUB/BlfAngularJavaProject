import {Component, ViewChild} from "@angular/core";
import { ModalComponent } from "./modal.component";

@Component({
  selector: 'app-modal-component',
  template: `
  <app-modal>
    <div class="app-modal-header">
    </div>
    <div class="app-modal-body">
    {{message}}
    </div>
    <div class="app-modal-footer">
      <button type="button" class="btn btn-default" (click)="modal.hide()">Close</button>
    </div>
  </app-modal>
  `
})
export class AppModalComponent {
  @ViewChild(ModalComponent)
  public modal: ModalComponent = new ModalComponent();
  message: string;

  public showModal(message): void {
    this.message = message;
    this.modal.show();
  }
}
