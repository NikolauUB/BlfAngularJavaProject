import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { VoteComponent }      from './vote/vote.component';
import {RegisterComponent} from "./auth/register/reqister.component";
import {ProfileComponent} from "./auth/profile/profile.component";
import {AboutComponent} from "./about/about.component";
import {HistoryComponent} from "app/history/history.component";
import {LoginComponent} from "./auth/login/login.component";
import {LoginvkComponent} from "./auth/login/loginvk.component";
import {ChangePasswordComponentComponent} from "./auth/changepassword/changepassword.component";
import {ChangeEmailComponent} from "./auth/changeemail/changeemail.component";
import {BaroqueComponent} from "./partaking/baroque/baroque.component";
import {JazzComponent} from "./partaking/jazz/jazz.component";
import {FreeComponent} from "./partaking/free/free.component";
import {ConcertComponent} from "./partaking/concert/concert.component";
import {CompositionComponent} from "./partaking/composition/composition.component";
import {RulesComponent} from "./rules/rules.component";
import {WelcomeComponent} from "./welcome/welcome.component";
import {StatisticComponent} from "./statistic/statistic.component";
import {AdminBaroqueComponent} from "./admin/baroque/admin.baroque.component";
import {AdminCompositionComponent} from "./admin/composition/admin.composition.component";
import {AdminJazzComponent} from "./admin/jazz/admin.jazz.component";
import {AdminFreeComponent} from "./admin/free/admin.free.component";
import {AdminConcertComponent} from "./admin/concert/admin.concert.component";
import {Error403Component} from "./errorpages/error403.component";
import {VoteBaroqueComponent} from "./vote/baroque/vote.baroque.component";
import {VoteJazzComponent} from "./vote/jazz/vote.jazz.component";
import {VoteFreeComponent} from "./vote/free/vote.free.component";
import {VoteConcertComponent} from "./vote/concert/vote.concert.component";
import {AdminVoteConcertComponent} from "./vote/concert/admin.vote.concert.component";
import {VoteCompositionComponent} from "./vote/composition/vote.composition.component";
import {NewstatisticComponent} from "./statistic/newstatistic.component";
import {ShowByidComponent} from "./vote/byid/show.byid.component";

const routes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '',     component: VoteCompositionComponent },
  { path: 'about',     component: AboutComponent },
  { path: 'history',     component: HistoryComponent },
  { path: 'voting',     component: VoteComponent },
  { path: 'registration',    component: RegisterComponent },
  { path: 'profile',    component: ProfileComponent },
  { path: 'baroque',     component: BaroqueComponent },
  { path: 'jazz',     component: JazzComponent },
  { path: 'free',     component: FreeComponent },
  { path: 'concert',     component: ConcertComponent },
  { path: 'composition',     component: CompositionComponent },
  { path: 'login', component: LoginComponent},
  { path: 'loginvk', component: LoginvkComponent}, //no apache  alias required
  { path: 'changePassword', component: ChangePasswordComponentComponent},
  { path: 'changeEmail', component: ChangeEmailComponent},
  { path: 'rules', component: RulesComponent},
  { path: 'welcome', component: WelcomeComponent},
  { path: 'blfRuStatistic', component: StatisticComponent},
  { path: 'adminBaroque', component: AdminBaroqueComponent},
  { path: 'adminComposition', component: AdminCompositionComponent},
  { path: 'adminJazz', component: AdminJazzComponent},
  { path: 'adminConcert', component: AdminConcertComponent},
  { path: 'adminFree', component: AdminFreeComponent},
  { path: 'voteBaroque', component: VoteBaroqueComponent},
  { path: 'voteJazz', component: VoteJazzComponent},
  { path: 'voteFree', component: VoteFreeComponent},
  { path: 'voteConcert', component: VoteConcertComponent},
  { path: 'adminVoteConcert', component: AdminVoteConcertComponent},
  { path: 'voteComposition', component: VoteCompositionComponent},
  { path: 'showByid/:id', component: ShowByidComponent},
  { path: 'newstatistic', component: NewstatisticComponent},
  { path: 'error403', component: Error403Component},

];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
