import {Component, OnInit} from "@angular/core";
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";
import {LoginData} from "../model/auth/LoginData";
import {AuthData} from "../model/auth/AuthData";

@Component({
  selector: 'login-app',
  templateUrl: './login.component.html',
  styleUrls: [ '../vote/voting.component.css' ]
})
export class LoginComponent implements OnInit {
  loginData:LoginData = new LoginData();
  errorMsg:string;

  constructor(private authService:AuthService,
              private router:Router) {
  }
  ngOnInit():void {
  }

  public doLogin(): void {
    this.authService
      .login(this.loginData)
      .then(authInfo => this.redirectToProfile(authInfo));
  }

  private redirectToProfile(authInfo: AuthData) {
    if (authInfo.code === 200 && authInfo.autheticated) {
      this.router.navigateByUrl('profile');
    } else if (authInfo.code === 400) {
      this.errorMsg = "Неправильный адрес электронной почты или пароль";
    } else if (authInfo.code === 500) {
      this.errorMsg = authInfo.errorMsg;
    }
  }


}
