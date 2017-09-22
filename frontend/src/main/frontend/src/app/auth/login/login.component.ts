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

  public onEnter(event: any): void {
    if(event.keyCode == 13) {
      this.doLogin();
    }
  }

  private redirectToProfile(authInfo: AuthData) {
    if (authInfo.cd === 200 && authInfo.auth) {
      this.router.navigateByUrl(this.returnUrl);
    } else if (authInfo.cd === 400) {
      this.errorMsg = "Неправильный адрес электронной почты / имя или пароль";
    } else if (authInfo.cd === 500) {
      this.errorMsg = authInfo.eMsg;
    }
  }


}
