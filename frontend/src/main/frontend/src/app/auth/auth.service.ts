import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import {AuthData} from "../model/auth/AuthData";
import {LoginData} from "../model/auth/LoginData";
import {QuestionData} from "app/model/auth/QuestionData";
import {RegistrationData} from "../model/auth/RegistrationData";
import {RegistrationReply} from "../model/auth/RegistrationReply";
import {ProfileChangedReply} from "../model/auth/ProfileChangedReply";
import {UserData} from "../model/auth/UserData";
import {PasswordData} from "../model/auth/PasswordData";
import {EmailData} from "../model/auth/EmailData";

@Injectable()
export class AuthService {
  private postHeaders: Headers;
  public auth: AuthData;
  private checkAuthUrl = 'api/checkAuth';
  private loginUrl = 'api/login';
  private logoutUrl = 'api/logout';
  private questionUrl = 'api/question';
  private registrationUrl = 'api/register';
  private profileUrl = 'api/profile';
  private saveProfileUrl = 'api/saveprofile';
  private deleteProfileImageUrl = 'api/deleteprofileimage';
  private changePasswordUrl = 'api/changepassword';
  private changePasswordByTidUrl = 'api/changepasswordtid';
  private changeemailUrl = 'api/changeemail';
  private userDetailsUrl = 'api/getUserDetails';


  constructor(private http: Http) {
    this.init();
  }

  public init(): Promise<any> {
    return this.checkAuth()
      .then(authInfo => this.auth = authInfo)
      .catch(this.handleError);
  }

  private setCSRFHeaders(token: string): void {
    this.postHeaders = new Headers({'Content-Type': 'application/json', 'X-CSRF-TOKEN': token});
  }

  private refreshAuth(authData: AuthData): AuthData {
      this.auth = authData;
      return this.auth;
  }


  public getAuth(): AuthData {
    return this.auth;
  }

  private checkAuth(): Promise<AuthData> {
    return this.http.get(this.checkAuthUrl)
      .toPromise()
      .then(response => response.json() as AuthData)
      .catch(this.handleError);
  }

  public getQuestion(): Promise<QuestionData> {
    return this.http.get(this.questionUrl)
      .toPromise()
      .then(response => response.json() as QuestionData)
      .catch(this.handleError);
  }

  /**
   * Loads current user profile
   *
   * @returns {Promise<UserData>}
   */
  public getProfile(): Promise<UserData> {
    return this.http.get(this.profileUrl)
        .toPromise()
        .then(response => response.json() as UserData)
        .catch(this.handleErrorWithoutAlerts);
  }

  /**
   * Used by user details controller which obtains avatar and username for any user by id
   *
   * @param {number} userId
   * @returns {Promise<UserData>}
   */
  public getUserDetails(userId: number): Promise<UserData> {
    return this.http.get(this.userDetailsUrl + "?uid=" + userId)
      .toPromise()
      .then(response => response.json() as UserData)
      .catch(this.handleErrorWithoutAlerts);
  }

  /**
   * Saves current user profile
   *
   * @param {UserData} userData
   * @returns {Promise<ProfileChangedReply>}
   */
  public saveProfile(userData: UserData): Promise<ProfileChangedReply> {
    this.setCSRFHeaders(this.auth.token);
    return this.http
        .put(this.saveProfileUrl,
        JSON.stringify(userData),
        {headers: this.postHeaders})
        .toPromise()
        .then(response => response.json() as ProfileChangedReply)
        .catch(this.handleErrorWithoutAlerts);
  }

  /**
   * Changes password for current user or by token from email
   *
   * @param {PasswordData} passwordData
   * @returns {Promise<string>}
   */
  public changePassword(passwordData: PasswordData): Promise<string> {
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .put((passwordData.token) ? this.changePasswordByTidUrl : this.changePasswordUrl,
        JSON.stringify(passwordData),
        {headers: this.postHeaders})
      .toPromise()
      .catch(this.handleErrorWithoutAlerts);
  }

  /**
   * Changes email for current user
   *
   * @param {EmailData} emailData
   * @returns {Promise<string>}
   */
  public changeEmail(emailData: EmailData): Promise<string> {
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .put(this.changeemailUrl,
        JSON.stringify(emailData),
        {headers: this.postHeaders})
      .toPromise()
      .catch(this.handleErrorWithoutAlerts);
  }

  /**
   * Deletes image from profile of current user
   * @returns {Promise<ProfileChangedReply>}
   */
  public deleteProfileImage(): Promise<ProfileChangedReply> {
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .delete(this.deleteProfileImageUrl,
        {headers: this.postHeaders})
      .toPromise()
      .then(response => response.json() as ProfileChangedReply)
      .catch(this.handleErrorWithoutAlerts);
  }

  public doRegistration(regData: RegistrationData): Promise<RegistrationReply> {
    return this.checkAuth()
      .then(authInfo => this.doSafeRegistration(regData, authInfo))
      .catch(this.handleError);
  }

  public login(loginData: LoginData): Promise<AuthData> {
    return this.checkAuth()
      .then(authInfo => this.safeLogin(loginData, authInfo))
      .catch(this.handleError);
  }

  public logout(): Promise<AuthData> {
    return this.checkAuth()
      .then(authInfo =>
        (authInfo.autheticated) ?
          this.safeLogout(authInfo) : this.refreshAuth(authInfo))
      .catch(this.handleError);
  }

  /**
   * Registration with refreshed csrf token
   * @param {RegistrationData} regData
   * @param {AuthData} authInfo
   * @returns {Promise<RegistrationReply>}
   */
  private doSafeRegistration(regData: RegistrationData,  authInfo: AuthData ): Promise<RegistrationReply> {
    this.auth = authInfo;
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .post(this.registrationUrl,
        JSON.stringify(regData),
        {headers: this.postHeaders})
      .toPromise()
      .then(response => response.json() as RegistrationReply)
      .catch(this.handleError);

  }

  /**
   * Login with refreshed csrf token
   * @param {LoginData} loginData
   * @param {AuthData} authInfo
   * @returns {Promise<AuthData>}
   */
  private safeLogin(loginData: LoginData, authInfo: AuthData): Promise<AuthData> {
    this.auth = authInfo;
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .post(this.loginUrl,
        JSON.stringify(loginData),
        {headers: this.postHeaders})
      .toPromise()
      .then(response => this.refreshAuth(response.json()))
      .catch(this.handleError);
  }

  /**
   * Logout with refreshed csrf token
   * @param {AuthData} authInfo
   * @returns {Promise<AuthData>}
   */
  private safeLogout(authInfo: AuthData): Promise<AuthData> {
    this.auth = authInfo;
    this.setCSRFHeaders(this.auth.token);
    return this.http
      .post(this.logoutUrl, null, {headers: this.postHeaders})
      .toPromise()
      .then(response => this.refreshAuth(response.json()))
      .catch(this.handleError);
  }

  /**
   * Handles any rest errors
   * @param error
   * @returns {Promise<any>}
   */
  private handleError(error: any): Promise<any> {
    console.error('An error occurred in Auth service', error);
    alert(error.message || error);
    return Promise.reject(error.message || error);
  }


  /**
   * Handles errors bound without alerts
   * @param error
   * @returns {Promise<any>}
   */
  private handleErrorWithoutAlerts(error: any): Promise<any> {
    console.error('An error occurred in Auth service', error);
    return Promise.reject(error);
  }
}
