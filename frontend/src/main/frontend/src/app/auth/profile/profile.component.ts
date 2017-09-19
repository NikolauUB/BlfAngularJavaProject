import {Component, OnInit, ViewChild} from "@angular/core";
import {Router} from "@angular/router";
import {UserData} from "../../model/auth/UserData";
import {AuthService} from "../auth.service";
import {ProfileChangedReply} from "../../model/auth/ProfileChangedReply";
import {NgForm} from "@angular/forms";
import {DetailsController} from "../userdetails/details.controller";


@Component({
  selector: 'profile-app',
  templateUrl: './profile.component.html',
  styleUrls: [ '../register/register.component.css' ]
})
export class ProfileComponent implements OnInit {
  @ViewChild('imagePath')
  imageInput: any;
  userData:UserData = new UserData();
  errorMsg:string;

  constructor(private authService:AuthService,
              private router:Router,
              private userDetailsController: DetailsController) {
  }

  ngOnInit():void {
      this.loadProfile(null);
  }

  public loadProfile(form: NgForm): void {
      this.errorMsg = "";
      this.authService
        .getProfile()
        .then(userData => this.userData = userData)
        .catch( e => this.handleError(e));
      if (form) {
        this.reset(form);
      }
  }


  private reset(form: NgForm): void {
    form.resetForm();
    if (this.imageInput.nativeElement) {
      this.imageInput.nativeElement.value = "";
    }
  }

  public saveProfile(form: NgForm): void {
      this.authService
        .saveProfile(this.userData)
        .then(reply => this.profileChangedReply(reply))
        .catch(e => this.handleError(e));
    this.reset(form);
  }

  public deleteProfileImage(form: NgForm): void {
    this.authService
      .deleteProfileImage()
      .then(reply => this.profileChangedReply(reply))
      .catch(e => this.handleError(e));
    this.reset(form);
  }

  private profileChangedReply(reply: ProfileChangedReply): void {
    let saveImage =  this.userData.saveImage;
    this.userData = reply.userData;
    this.errorMsg = "";
    if (reply.code !== 200) {
      this.errorMsg = reply.errorMsg;
      if(saveImage != null && saveImage.length > 0) {
        this.userData.saveImage = saveImage;
      }
    } else {
      this.userDetailsController.cleanUserDetails(this.authService.getAuth().uId);
    }
  }


  fileChangeEvent(fileInput:any, userData:UserData, imagePreview) {
    var root = this;
    this.errorMsg = "";
    if (fileInput.target.files && fileInput.target.files[0]) {
      var reader = new FileReader();
      var img = fileInput.target.files[0];
      //check image/jpeg, image/png
      if (img.type === 'image/jpeg'
          || img.type === 'image/png') {
        imagePreview.src ="#";
        userData.saveImage = null;
        reader.onload = function (e:any) {
          var testImg = document.createElement("img");
          testImg.onload = function () {
            var dim = {width:0, height:0};
            root.calcImageSize(dim, testImg);
            var canvas = document.createElement("canvas");
            canvas.height = dim.height;
            canvas.width = dim.width;

            // Copy the image contents to the canvas
            var ctx = canvas.getContext("2d");
            ctx.drawImage(testImg, 0, 0, dim.width, dim.height);
            userData.previewImage = canvas.toDataURL('image/jpeg');
            imagePreview.src = userData.previewImage;
            userData.saveImage = userData.previewImage.replace(/^data:image\/(jpeg);base64,/, "");
          }
          testImg.src = e.target.result;
        }
        reader.readAsDataURL(fileInput.target.files[0]);
      } else {
        this.errorMsg = "Картинка должна быть в формате jpeg или png!";
        imagePreview.src = userData.previewImage;
      }
    }
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

  private handleError(e: any) : void {
    if(e.status === 403) {
      alert("Ваша сессия не активна. Пожалуйста, зайдите на сайт снова!");
      this.router.navigateByUrl("login");
    } else {
      alert(e);
    }
  }

}
