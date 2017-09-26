import {Injectable} from "@angular/core";
import {AngularIndexedDB} from 'angular2-indexeddb';
import {DiscussionItem} from "app/model/DiscussionItem";

@Injectable()
export class ThemeController {
    private db: AngularIndexedDB = new AngularIndexedDB('ThemeStatus', 1);

    constructor() {
        //this.createStore();
    }


    public createStore(): void {
        this.db.createStore(1, (evt) => {
            let objectStore = evt.currentTarget.result.createObjectStore(
                'themestatus', {keyPath: "id", autoIncrement: false});

            objectStore.createIndex("updated", "updated", {unique: false});
            objectStore.createIndex("msgs", "msgs", {unique: false});
        });
    }

    public createStoreAndLoadMaxUpdated(threadId: number): Promise<Date> {
        return this.db.createStore(1, (evt) => {
            let objectStore = evt.currentTarget.result.createObjectStore(
                'themestatus', {keyPath: "id", autoIncrement: false});

            objectStore.createIndex("updated", "updated", {unique: false});
            objectStore.createIndex("msgs", "msgs", {unique: false});
        }).then(e=>this.loadUpdatedByThemeId(threadId));
    }

    public saveThemeInDBbyId(discussionItems: Array<DiscussionItem>, threadId: number, thUpdated: Date) {
        this.db.add('themestatus',
            { id: threadId,
                updated: thUpdated,
                msgs: JSON.stringify(discussionItems)})
            .catch(e => console.log(e));
    }

    public cleanTheme(threadId: number): void {
        this.db.delete('themestatus', threadId).catch(e=> console.log(e));
    }

    public loadThemeById(threadId: number): Promise<Array<DiscussionItem>> {
        return this.db.getByKey('themestatus', threadId)
            .then((details) => {
                return (details != null) ? JSON.parse(details.msgs) : null;
            }, (error) => {
                console.log(error);
            });
    }

    public loadUpdatedByThemeId(threadId: number): Promise<Date> {
        return this.db.getByKey('themestatus', threadId)
            .then((details) => {
                return (details != null) ? details.updated : 0;
            }, (error) => {
                console.log(error);
            });
    }
}