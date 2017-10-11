import {Component, OnInit} from '@angular/core';
import {NavigationError, Router} from "@angular/router";
import {AuthService} from "./auth/auth.service";
import {AuthData} from "./model/auth/AuthData";
import {ChangesController} from "./changescontrol/changes.controller";

@Component({
  selector: 'app-root',
  templateUrl: './main.html',
  styleUrls: [ "./app.component.css"]
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
    return (this.auth.getAuth()) ? this.auth.getAuth().auth : false;
  }

  public getUserName(): string {
    return (this.auth.getAuth()) ? this.auth.getAuth().uName : "";
  }

  public doLogout(): void {
    this.auth
      .logout()
      .then(authData => this.checkLogoutErrors(authData));
  }

  public cleanCache(): void {
    localStorage.removeItem(ChangesController.PREVIOUS_TIME);
    window.location.reload();
  }

  private checkLogoutErrors(authData: AuthData) {
    if (authData.cd !== 200) {
      alert("Ошибка при попытке выйти! " + authData.eMsg);
    } else {
      this.router.navigateByUrl('login');
    }
  }
}
