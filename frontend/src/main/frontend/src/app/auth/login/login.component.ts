import {Component, OnInit} from "@angular/core";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginData} from "../../model/auth/LoginData";
import {AuthData} from "../../model/auth/AuthData";

@Component({
  selector: 'login-app',
  templateUrl: './login.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})
export class LoginComponent implements OnInit {
  private static defaultUrl: string = 'profile';
  private static defaultLoginTitle: string = "Вход в личный кабинет";
  loginData:LoginData = new LoginData();
  errorMsg: string;
  returnUrl: string = LoginComponent.defaultUrl;
  loginTitle: string = LoginComponent.defaultLoginTitle;

  constructor(private authService:AuthService,
              private router:Router,
              private route: ActivatedRoute) {
  }
  ngOnInit():void {
    if(this.route.snapshot.queryParams['returnUrl'] != null) {
      this.returnUrl = this.route.snapshot.queryParams['returnUrl'];
      this.loginTitle = "Вход в систему";
    } else {
      this.returnUrl = LoginComponent.defaultUrl;
      this.loginTitle = LoginComponent.defaultLoginTitle;
    }

  }

  public doLogin(): void {
    this.authService
      .login(this.loginData)
      .then(authInfo => this.redirectToProfile(authInfo));
  }

  private redirectToProfile(authInfo: AuthData) {
    if (authInfo.code === 200 && authInfo.autheticated) {
      this.router.navigateByUrl(this.returnUrl);
    } else if (authInfo.code === 400) {
      this.errorMsg = "Неправильный адрес электронной почты или пароль";
    } else if (authInfo.code === 500) {
      this.errorMsg = authInfo.errorMsg;
    }
  }


}
