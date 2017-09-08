import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { VoteComponent }      from './vote/vote.component';
import {RegisterComponent} from "./auth/register/reqister.component";
import {ProfileComponent} from "./auth/profile/profile.component";
import {AboutComponent} from "./about/about.component";
import {HistoryComponent} from "app/history/history.component";
import {LoginComponent} from "./auth/login/login.component";
import {ChangePasswordComponentComponent} from "./auth/changepassword/changepassword.component";
import {ChangeEmailComponent} from "./auth/changeemail/changeemail.component";
import {BaroqueComponent} from "./partaking/baroque/baroque.component";
import {JazzComponent} from "./partaking/jazz/jazz.component";
import {FreeComponent} from "./partaking/free/free.component";
import {CompositionComponent} from "./partaking/composition/composition.component";
import {RulesComponent} from "./rules/rules.component";
import {WelcomeComponent} from "./welcome/welcome.component";

const routes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '',     component: AboutComponent },
  { path: 'about',     component: AboutComponent },
  { path: 'history',     component: HistoryComponent },
  { path: 'voting',     component: VoteComponent },
  { path: 'registration',    component: RegisterComponent },
  { path: 'profile',    component: ProfileComponent },
  { path: 'baroque',     component: BaroqueComponent },
  { path: 'jazz',     component: JazzComponent },
  { path: 'free',     component: FreeComponent },
  { path: 'composition',     component: CompositionComponent },
  { path: 'login', component: LoginComponent},
  { path: 'changePassword', component: ChangePasswordComponentComponent},
  { path: 'changeEmail', component: ChangeEmailComponent},
  { path: 'rules', component: RulesComponent},
  { path: 'welcome', component: WelcomeComponent}

];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
