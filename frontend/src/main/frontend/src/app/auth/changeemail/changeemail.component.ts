import {Component} from "@angular/core";
import {EmailData} from "../../model/auth/EmailData";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'change-email-app',
  templateUrl: './changeemail.component.html',
  styleUrls: [ '../register/register.component.css' ]
})
export class ChangeEmailComponent {
  emailData: EmailData = new EmailData();
  commonErrorMsg: string;

  constructor(private authService:AuthService,
              private router:Router) {
  }

  public changeEmail(): void {
    this.commonErrorMsg = "";
    this.authService
      .changeEmail(this.emailData)
      .then(reply => this.router.navigateByUrl("profile"))
      .catch(e => this.handleError(e));
  }

  private handleError(e: any) : void {
    if(e.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт снова!");
      this.router.navigateByUrl("login");
    } else {
      this.commonErrorMsg = e.json().message || e.toString();
    }
  }
}
