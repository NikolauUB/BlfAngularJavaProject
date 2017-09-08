import {Injectable} from "@angular/core";
import {CompetitionMember} from "../model/CompetitionMember";

@Injectable()
export class CompetitionShortInfo {
  public static TYPE_PRESCRIBED_BAROQUE: number = 0;
  public static TYPE_PRESCRIBED_JAZZ: number = 1;
  public static TYPE_FREE: number = 2;
  public static TYPE_COMPOSITION: number = 3;
  compType: number;
  compId: number;
  membersForShow: Array<CompetitionMember> = new Array<CompetitionMember>();
  userChoosePrescribeProgramm: number = -1;
  userThread: number = -1;

  public isUserChooseAlternativeProgramm(): boolean {
    if (this.userChoosePrescribeProgramm !== -1) {
      return (this.compType === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE
              && this.userChoosePrescribeProgramm === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ)
              ||
              (this.compType === CompetitionShortInfo.TYPE_PRESCRIBED_JAZZ
              && this.userChoosePrescribeProgramm === CompetitionShortInfo.TYPE_PRESCRIBED_BAROQUE);
    } else {
      return false;
    }
  }
}
