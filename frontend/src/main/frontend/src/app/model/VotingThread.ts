import {DiscussionItem} from "./DiscussionItem";

export class VotingThread {
    oi: Array<DiscussionItem> = new Array<DiscussionItem>();
    id: number;
    //Id of younger item of the page. Previous page will start after it
    yid: number;
    //Count of younger items -1 default
    yc: number;
    //Count of all items
    ac: number;
    //Was changed from control date or not
    c: boolean;
}