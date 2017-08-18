import {VoteData} from "./VoteData";
import {CompetitionData} from "./CompetitionData";
export class CompetitionInfo {
  code: number;
  errorMsg: string;
  competitionData: CompetitionData;
  voteData: Array<VoteData>;

}
