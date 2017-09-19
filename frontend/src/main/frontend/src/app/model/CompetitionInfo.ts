import {VoteData} from "./VoteData";
import {CompetitionData} from "./CompetitionData";
export class CompetitionInfo {
  competitionData: CompetitionData = new CompetitionData();
  voted: boolean = false;
  voteData: Array<VoteData> = new Array<VoteData>();

}
