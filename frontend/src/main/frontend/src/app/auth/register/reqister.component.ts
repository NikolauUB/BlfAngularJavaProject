import {Component, OnInit, ViewChild} from "@angular/core";
import {Router} from "@angular/router";
import {AuthData} from "../../model/auth/AuthData";
import {QuestionData} from "../../model/auth/QuestionData";
import {RegistrationData} from "../../model/auth/RegistrationData";
import {RegistrationReply} from "../../model/auth/RegistrationReply";
import {LoginData} from "../../model/auth/LoginData";
import {AuthService} from "../auth.service";

@Component({
  selector: 'reqister-app',
  templateUrl: './register.component.html',
  styleUrls: [ './register.component.css' ]
})
export class RegisterComponent implements OnInit {
  questionData: QuestionData;
  loginData: LoginData = new LoginData();
  registrationData: RegistrationData = new RegistrationData();
  selectedAnswers: Set<string> = new Set<string>();

  //errors from backend
  commonErrorMsg: string;




  constructor(private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.getQuestion();
  }


  private getQuestion(): void {
    this.authService
      .getQuestion()
      .then(questionData => this.refreshQuestion(questionData));
  }

  refreshQuestion(questionData): void {
    this.questionData = questionData;
  }

  public doReqistration(): void {
    //delete previous errors
    this.commonErrorMsg = "";
    this.registrationData.givenAnswers = [];
    //new answers
    this.selectedAnswers.forEach(item => this.registrationData.givenAnswers.push(item));
    this.authService
      .doRegistration(this.registrationData)
      .then(reply => this.parseRegistrationReply(reply));
  }

  private parseRegistrationReply(reply: RegistrationReply): void {
    if  (reply.code !== 200) {
      this.commonErrorMsg = reply.errorMsg;
      let email =  this.registrationData.email;
      let password =  this.registrationData.password;
      this.questionData = reply.newQuestion;
      this.registrationData = reply.registrationData;
      this.registrationData.email = email;
      this.registrationData.password = password;
    } else {
      this.doLogin();
      this.router.navigateByUrl("profile");
    }
    this.selectedAnswers = new Set<string>();
  }

  doLogin(): void {
    this.loginData.email = this.registrationData.email;
    this.loginData.password = this.registrationData.password;
    this.authService.login(this.loginData);
  }

  onSelect(answerSelected: string): void {
    if (this.selectedAnswers.has(answerSelected)) {
      this.selectedAnswers.delete(answerSelected);
    } else {
      this.selectedAnswers.add(answerSelected);
    }
  }
}
