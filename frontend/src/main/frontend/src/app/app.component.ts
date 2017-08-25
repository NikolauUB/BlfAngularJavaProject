import {Component, OnInit} from '@angular/core';
import {NavigationError, Router} from "@angular/router";
import {AuthService} from "./auth/auth.service";
import {AuthData} from "./model/auth/AuthData";

@Component({
  selector: 'app-root',
  templateUrl: './main.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent{
  auth:  AuthService;

  constructor(private router: Router,
              private authService: AuthService) {
    this.auth = authService;
    this.router.events.subscribe(routerEvent  => {
      if (routerEvent instanceof NavigationError) {
        this.router.navigate(["about"], {skipLocationChange: true})
      }
    });
  }

  public isUsersAuthentificated(): boolean {
    return (this.auth.getAuth()) ? this.auth.getAuth().autheticated : false;
  }

  public getUserName(): string {
    return (this.auth.getAuth()) ? this.auth.getAuth().username : "";
  }

  public doLogout(): void {
    this.auth
      .logout()
      .then(authData => this.checkLogoutErrors(authData));
  }

  private checkLogoutErrors(authData: AuthData) {
    if (authData.code !== 200) {
      alert("Ошибка при попытке выйти! " + authData.errorMsg);
    } else {
      this.router.navigateByUrl('login');
    }
  }
}
