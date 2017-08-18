import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { HttpModule }    from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';

import { VoteComponent }      from './vote/vote.component';
import { VoteService }          from './vote/vote.service';
import { ModalComponent } from "./modal/modal.component";
import {AppModalComponent} from "./modal/appmodal.component";
import {RegisterComponent} from "./auth/reqister.component";
import {ProfileComponent} from "./auth/profile.component";
import {AuthService} from "./auth/auth.service";
import {PartakingComponent} from "./partaking/partaking.component";
import {PartakingService} from "./partaking/partaking.service";
import {AboutComponent} from "./about/about.component";
import {HistoryComponent} from "./history/history.component";
import {LoginComponent} from "./auth/login.component";
import {PasswordConfirmValidator} from "./auth/PasswordConfirmValidator";
import {ChangePasswordComponentComponent} from "./auth/changepassword.component";
import {ChangeEmailComponent} from "./auth/changeemail.component";

@NgModule({
  declarations: [
    AboutComponent,
    HistoryComponent,
    AppComponent,
    VoteComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    PasswordConfirmValidator,
    ChangePasswordComponentComponent,
    ChangeEmailComponent,
    PartakingComponent,
    ModalComponent,
    AppModalComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpModule,
  ],
  providers: [VoteService, AuthService, PartakingService],
  bootstrap: [AppComponent]
})
export class AppModule { }
