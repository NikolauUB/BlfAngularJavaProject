import {Component, OnDestroy, OnInit} from "@angular/core";
import {PasswordData} from "../../model/auth/PasswordData";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Subscription} from "rxjs/Subscription";

@Component({
  selector: 'change-password-app',
  templateUrl: './changepassword.component.html',
  styleUrls: [ '../register/register.component.css' ]
})
export class ChangePasswordComponentComponent implements OnInit, OnDestroy {
  passwordData: PasswordData = new PasswordData();
  commonErrorMsg: string;
  private token: string;
  private sub: Subscription;

  constructor(private authService:AuthService,
              private route: ActivatedRoute,
              private router:Router) {
  }

  ngOnInit() {
    this.sub = this.route.queryParams.subscribe((params: Params) => {
      this.token = params['tid'];
      if (this.token) {
        this.passwordData.token = this.token;
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  public changePassword(): void {
      this.commonErrorMsg = "";
      this.authService
        .changePassword(this.passwordData)
        .then(reply => this.router.navigateByUrl("profile"))
        .catch(e => this.handleError(e));
  }

  private handleError(e: any) : void {
    if(e.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт снова!");
      this.router.navigateByUrl("login");
    } else {
      this.commonErrorMsg = e.json().message;
    }
  }

}
