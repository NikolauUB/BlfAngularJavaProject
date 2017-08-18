import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { VoteComponent }      from './vote/vote.component';
import {RegisterComponent} from "./auth/reqister.component";
import {ProfileComponent} from "./auth/profile.component";
import {PartakingComponent} from "./partaking/partaking.component";
import {AboutComponent} from "./about/about.component";
import {HistoryComponent} from "app/history/history.component";
import {LoginComponent} from "./auth/login.component";
import {ChangePasswordComponentComponent} from "./auth/changepassword.component";
import {ChangeEmailComponent} from "./auth/changeemail.component";

const routes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '',     component: AboutComponent },
  { path: 'about',     component: AboutComponent },
  { path: 'history',     component: HistoryComponent },
  { path: 'voting',     component: VoteComponent },
  { path: 'registration',    component: RegisterComponent },
  { path: 'profile',    component: ProfileComponent },
  { path: 'partake',     component: PartakingComponent },
  { path: 'login', component: LoginComponent},
  { path: 'changePassword', component: ChangePasswordComponentComponent},
  { path: 'changeEmail', component: ChangeEmailComponent}

];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
