import {DiscussionItem} from "../model/DiscussionItem";

export interface EditInterface {
   saveItem(discussionItem: DiscussionItem): void;
}
