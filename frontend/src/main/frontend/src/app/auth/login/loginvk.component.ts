import {Component, OnInit} from "@angular/core";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthData} from "../../model/auth/AuthData";
import {ChangesController} from "../../changescontrol/changes.controller";
import {LoginComponent} from "./login.component";
import {RegistrationData} from "../../model/auth/RegistrationData";
import {RegistrationReply} from "../../model/auth/RegistrationReply";
import {QuestionData} from "../../model/auth/QuestionData";
import {LoginData} from "../../model/auth/LoginData";


@Component({
  selector: 'loginvk-app',
  templateUrl: './loginvk.component.html',
  styleUrls: [ '../../vote/voting.component.css' ]
})
export class LoginvkComponent extends LoginComponent implements OnInit {
  registrationData: RegistrationData = new RegistrationData();
  loginData: LoginData = new LoginData();
  uid: number;
  firstName: string = "";
  lastName: string = "";
  photoUrl: string = null;
  hash: string = null;
  saveImageStr: string = "";
  isToBind: boolean = true;
  isPictureChecked: boolean = false;


  questionData: QuestionData = new QuestionData();
  selectedAnswers: Set<string> = new Set<string>();


  ngOnInit():void {
    this.uid = this.route.snapshot.queryParams['uid'];
    this.firstName = this.route.snapshot.queryParams['first_name'];
    this.lastName = this.route.snapshot.queryParams['last_name'];
    this.photoUrl = this.route.snapshot.queryParams['photo'];
    this.hash = this.route.snapshot.queryParams['hash'];
    this.registrationData.username = this.firstName + ' ' + this.lastName;
    this.registrationData.username = this.registrationData.username.trim();

    this.checkBinding(this.uid, this.hash);
    this.authService
              .getQuestion()
              .then(questionData => this.questionData = questionData);
  }

  private checkBinding(uid: number, hash: string): void {
    this.authService.checkVKHash(uid, hash)
      .then(authInfo => this.redirectToProfile(authInfo));

  }

  public doBindVK(): void {
    this.authService.bindToVK(this.uid, this.hash, this.loginData)
          .then(authInfo => this.redirectToProfile(authInfo))
          .catch(e => this.handleVKError(e));
  }


  public doCreateFromVK(): void {
    //delete previous errors
    this.errorMsg = "";
    this.registrationData.givenAnswers = [];
    //new answers
    this.selectedAnswers.forEach(item => this.registrationData.givenAnswers.push(item));

    this.registrationData.vkId = this.uid;
    this.registrationData.vkHash = this.hash;
    this.authService
      .doRegistration(this.registrationData)
      .then(reply => this.parseRegistrationReply(reply));
  }


  private parseRegistrationReply(reply: RegistrationReply): void {
      if  (reply.code !== 200) {
        this.errorMsg = reply.errorMsg;
        this.questionData = reply.newQuestion;
      } else {
        //clean vote cache
        localStorage.removeItem(ChangesController.VOTING_CLASSIC);
        localStorage.removeItem(ChangesController.VOTING_JAZZ);
        localStorage.removeItem(ChangesController.VOTING_COMPOSITION);
        localStorage.removeItem(ChangesController.VOTING_FREE);
        this.checkBinding(this.uid, this.hash);
      }
      this.selectedAnswers = new Set<string>();
  }

  public loadImageFromVK(imagePreview, useVKPicture): void  {
       if (!useVKPicture.checked) {
          imagePreview.src = "";
          this.registrationData.img = "";
          return;
       }
       var root = this;
       var testImg = document.createElement("img");
       testImg.onload = function () {
           var dim = {width:0, height:0};
           root.calcImageSize(dim, testImg);
           var canvas = document.createElement("canvas");
           canvas.height = dim.height;
           canvas.width = dim.width;
           var ctx = canvas.getContext("2d");
           ctx.drawImage(testImg, 0, 0, dim.width, dim.height);
           let imgString = canvas.toDataURL('image/jpeg');
           imagePreview.src = imgString;
           root.registrationData.img = imgString.replace(/^data:image\/(jpeg);base64,/, "");
       }
       testImg.setAttribute('crossOrigin', 'anonymous');
       testImg.src = this.photoUrl;
  }

  private calcImageSize(dim, testImg): void {
      if (testImg.width >= testImg.height && testImg.width > 200) {
        var ratio = testImg.width / 200;
        dim.width = 200;
        dim.height = testImg.height / ratio;
      } else if (testImg.height > testImg.width && testImg.height > 200) {
        var ratio = testImg.height / 200;
        dim.height = 200;
        dim.width = testImg.width / ratio;
      } else {
        dim.height = testImg.height;
        dim.width = testImg.width;
      }
  }

  onSelect(answerSelected: string): void {
      if (this.selectedAnswers.has(answerSelected)) {
        this.selectedAnswers.delete(answerSelected);
      } else {
        this.selectedAnswers.add(answerSelected);
      }
  }
  private handleVKError(e: any) : void {
      this.errorMsg = e.message||e;
  }

}
