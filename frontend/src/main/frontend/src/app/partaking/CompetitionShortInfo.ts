import {Injectable} from "@angular/core";

@Injectable()
export class CompetitionShortInfo {
  public static TYPE_PRESCRIBED_BAROQUE: number = 0;
  public static TYPE_PRESCRIBED_JAZZ: number = 1;
  public static TYPE_FREE: number = 2;
  public static TYPE_COMPOSITION: number = 3;
  compType: number;
  compId: number;
}
