import { UserData } from './auth/UserData';
export class DiscussionItem {
    competitionId: number;
    msgThreadId: number;
    msgId: number;
    parentMsgId: number;
    authorId: number;
    msgText: string;
    creationDate: Date;
    updateDate: Date;
    authorUsername: string = "";
    authorAvatar: string = "";
    authorDetails: UserData = new UserData();
}
