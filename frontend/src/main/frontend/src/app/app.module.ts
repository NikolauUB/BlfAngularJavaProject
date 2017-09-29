import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import {APP_INITIALIZER, LOCALE_ID, NgModule} from '@angular/core';
import { HttpModule }    from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';

import { VoteComponent }      from './vote/vote.component';
import { VoteService }          from './vote/vote.service';
import { ModalComponent } from "./modal/modal.component";
import { EditModalComponent} from "./modal/editmodal.component";
import {ItemdetailsComponent} from "./modal/itemdetails.component";
import { ShowpictureComponent} from "./modal/showpicture.component";
import {RegisterComponent} from "./auth/register/reqister.component";
import {ProfileComponent} from "./auth/profile/profile.component";
import {AuthService} from "./auth/auth.service";
import {PartakingService} from "./partaking/partaking.service";
import {AboutComponent} from "./about/about.component";
import {RulesComponent} from "./rules/rules.component";
import {HistoryComponent} from "./history/history.component";
import {LoginComponent} from "./auth/login/login.component";
import {PasswordConfirmValidator} from "./auth/PasswordConfirmValidator";
import {ChangePasswordComponentComponent} from "./auth/changepassword/changepassword.component";
import {ChangeEmailComponent} from "./auth/changeemail/changeemail.component";
import {BaroqueComponent} from "./partaking/baroque/baroque.component";
import {CompetitionComponent} from "./partaking/competition.component";
import {CompetitionShortInfo} from "./partaking/CompetitionShortInfo";
import {ListComponent} from "./partaking/menu/list.component";
import {JazzComponent} from "./partaking/jazz/jazz.component";
import {FreeComponent} from "./partaking/free/free.component";
import {CompositionComponent} from "./partaking/composition/composition.component";
import {ChangesService} from "./changescontrol/changes.service";
import {ChangesController} from "./changescontrol/changes.controller";
import {DiscussionComponent} from "./discussion/discussion.component";
import {WelcomeComponent} from "./welcome/welcome.component";
import {StatisticComponent} from "./statistic/statistic.component";
import {SafePipe} from "./safe.pipe";
import {DetailsController} from "./auth/userdetails/details.controller";
import {ThemeController} from "./theme/theme.controller";
import {AdminComponent} from "./admin/admin.component";
import {AdminBaroqueComponent} from "./admin/baroque/admin.baroque.component";
import {AdminCompositionComponent} from "./admin/composition/admin.composition.component";
import {AdminJazzComponent} from "./admin/jazz/admin.jazz.component";
import {AdminFreeComponent} from "./admin/free/admin.free.component";
import {AdminService} from "./admin/admin.service";
import {Error403Component} from "./errorpages/error403.component";
import {VoteMenuComponent} from "./vote/menu/vote.menu.component";
import {VoteBaroqueComponent} from "./vote/baroque/vote.baroque.component";
import {VoteJazzComponent} from "./vote/jazz/vote.jazz.component";
import {VoteFreeComponent} from "./vote/free/vote.free.component";
import {VoteCompositionComponent} from "./vote/composition/vote.composition.component";
import { Ng2DeviceDetectorModule } from 'ng2-device-detector';




/**
 * This service checks database for changes of seldom changed data and if it is changed cleans localStorage
 * @param {ChangesController} service
 * @returns {() => any}
 */
export function runChangesController(changesService: ChangesController): () => Promise<void>  {
  return () => changesService.init();
}

@NgModule({
  declarations: [
    SafePipe,
    AboutComponent,
    AdminComponent,
    AdminBaroqueComponent,
    AdminCompositionComponent,
    AdminJazzComponent,
    AdminFreeComponent,
    HistoryComponent,
    AppComponent,
    VoteComponent,
    VoteMenuComponent,
    VoteBaroqueComponent,
    VoteJazzComponent,
    VoteFreeComponent,
    VoteCompositionComponent,
    RulesComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    CompetitionComponent,
    ListComponent,
    PasswordConfirmValidator,
    ChangePasswordComponentComponent,
    ChangeEmailComponent,
    DiscussionComponent,
    BaroqueComponent,
    JazzComponent,
    FreeComponent,
    CompositionComponent,
    ModalComponent,
    EditModalComponent,
    ItemdetailsComponent,
    ShowpictureComponent,
    WelcomeComponent,
    StatisticComponent,
    Error403Component
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpModule,
    Ng2DeviceDetectorModule.forRoot()
  ],
  providers: [
    VoteService,
    AuthService,
    AdminService,
    PartakingService,
    CompetitionShortInfo,
    DetailsController,
    ChangesService,
    ChangesController,
    ThemeController,
    {
      provide: APP_INITIALIZER,
      useFactory: runChangesController,
      deps: [ChangesController],
      multi: true
    },
    {provide: LOCALE_ID, useValue: "ru" }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
