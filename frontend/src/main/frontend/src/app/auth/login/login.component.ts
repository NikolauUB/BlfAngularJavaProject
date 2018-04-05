import {Component, OnInit} from "@angular/core";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginData} from "../../model/auth/LoginData";
import {AuthData} from "../../model/auth/AuthData";
import {ChangesController} from "../../changescontrol/changes.controller";
import {CompetitionInfo} from "../../model/CompetitionInfo";
declare var VK :any;

@Component({
  selector: 'login-app',
  templateUrl: './login.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})
export class LoginComponent implements OnInit {
  protected static defaultUrl: string = 'profile';
  private static defaultLoginTitle: string = "Вход в личный кабинет";
  loginData:LoginData = new LoginData();
  errorMsg: string;
  returnUrl: string = LoginComponent.defaultUrl;
  loginTitle: string = LoginComponent.defaultLoginTitle;

  constructor(protected authService:AuthService,
              protected router:Router,
              protected route: ActivatedRoute) {
  }
  ngOnInit():void {
    if(this.route.snapshot.queryParams['returnUrl'] != null) {
      this.returnUrl = this.route.snapshot.queryParams['returnUrl'];
      this.loginTitle = "Вход в систему";
    } else {
      this.returnUrl = LoginComponent.defaultUrl;
      this.loginTitle = LoginComponent.defaultLoginTitle;
    }
    //VK.Widgets.Auth("vk_auth", {"authUrl":"/dev/Login"});
  }
  public testRedirectVK(): void {
     this.router.navigateByUrl("/loginvk?uid=3844960&first_name=%D0%95%D0%BB%D0%B5%D0%BD%D0%B0&last_name=%D0%A5%D0%BE%D0%BB%D0%BE%D0%B4%D0%B8%D0%BB%D0%BE%D0%B2%D0%B0&photo=https://pp.userapi.com/c113/u3844960/a_d6342567.jpg&photo_rec=https://pp.userapi.com/c113/u3844960/e_7974b37e.jpg&hash=05fa65ffd5917c8d8aa5f311ad7db0d0");
     //this.router.navigateByUrl("/loginvk?uid=4648803&first_name=Николай&last_name=Барабанщиков&photo=https://pp.userapi.com/c626723/v626723803/4144b/bJnH5CZlSpQ.jpg&photo_rec=https://pp.userapi.com/c626723/v626723803/4144e/WSHczuyjOpg.jpg&hash=d469937abb106928dab321dd4b26b37b");
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

  protected redirectToProfile(authInfo: AuthData) {
    if (authInfo.cd === 200 && authInfo.auth) {
      this.cleanVotingCaches(authInfo);
      this.router.navigateByUrl(this.returnUrl);
    } else if (authInfo.cd === 400) {
      this.errorMsg = "Неправильный адрес электронной почты / имя или пароль";
    } else if (authInfo.cd === 404) {
      this.errorMsg = "Пользователь не найден";
    } else if (authInfo.cd === 500) {
      this.errorMsg = authInfo.eMsg;
    }
  }

  public cleanVotingCaches(authInfo: AuthData): void {
    this.cleanVotingCache(localStorage.getItem(ChangesController.VOTING_CLASSIC), authInfo);
    this.cleanVotingCache(localStorage.getItem(ChangesController.VOTING_JAZZ), authInfo);
    this.cleanVotingCache(localStorage.getItem(ChangesController.VOTING_COMPOSITION), authInfo);
    this.cleanVotingCache(localStorage.getItem(ChangesController.VOTING_FREE), authInfo);
    this.cleanVotingCache(localStorage.getItem(ChangesController.VOTING_CONCERT), authInfo);
  }

  private cleanVotingCache(json: string, authInfo: AuthData): void {
    var competitionInfo: CompetitionInfo = null;
    if (json != null) {
      competitionInfo = JSON.parse(json);
    }
    if (competitionInfo != null && authInfo.uId !== competitionInfo.userId) {
      localStorage.removeItem(ChangesController.VOTING_PREFIX + competitionInfo.competitionData.type);
    }
  }


}
