import { UserCompetition } from './UserCompetition';
export class UserStatisticHistory {
  userId: number;
  username: string;
  leaves: number;
  broomType: number;
  compIds: Array<UserCompetition> = new Array<UserCompetition>();
}
