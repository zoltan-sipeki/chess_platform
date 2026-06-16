import { inject, Injectable, signal, WritableSignal } from "@angular/core";
import { Observable, tap } from "rxjs";
import { EventService } from "./EventService";
import { FriendApi, FriendList } from "./FriendApi";
import { RelayService } from "./RelayService";
import { UserData } from "./UserApi";

@Injectable({
    providedIn: "root",
})
export class FriendService {

    private api: FriendApi = inject(FriendApi);

    private eventService: EventService = inject(EventService);

    private relayService: RelayService = inject(RelayService);

    private friends: UserData[] = [];

    private _online = signal<UserData[]>([]);

    private _offline = signal<UserData[]>([]);

    readonly online = this._online.asReadonly();

    readonly offline = this._offline.asReadonly();

    constructor() {
        this.relayService.subscribe("UNFRIEND", e => {
            const { senderId } = e;
            this.friends = this.friends.filter(friend => friend.id !== senderId);
            this._online.update(friends => friends.filter(friend => friend.id !== senderId));
            this._offline.update(friends => friends.filter(friend => friend.id !== senderId));
        });

        this.relayService.subscribe("USER_UPDATED", e => {
            let f = this.friends.find(f => f.id === e.id);
            if (f != null) {
                f.displayName = e.displayName;
                f.avatar = e.avatar;
            }

            this._online.set([...this._online()]);
            this._offline.set([...this._offline()]);
        });

        this.relayService.subscribe("PRESENCE_CHANGED", e => {
            let f = this.friends.find(f => f.id === e.userId);
            if (f == null) {
                return;
            }
            const oldPresence = f.presence;;
            f.presence = e.presence;

            if (e.presence === "OFFLINE") {
                this._online.update(friends => friends.filter(friend => friend.id !== e.userId));
                this.add(f, this._offline);
            }
            else {
                if (oldPresence === "OFFLINE") {
                    this._offline.update(friends => friends.filter(friend => friend.id !== e.userId));
                    this.add(f, this._online);

                }
                else {
                    this._online.set([...this._online()]);
                }
            }
        });
    }

    addFriend(friend: UserData): void {
        this.friends.push(friend);
        if (friend.presence === "ONLINE" || friend.presence === "AWAY") {
            this.add(friend, this._online,);
        }
        else {
            this.add(friend, this._offline);
        }
    }

    private add(friend: UserData, list: WritableSignal<UserData[]>): void {
        list.update(friends => {
            const arr = [...friends];
            this.insert(friend, arr);
            return arr;
        });
    }

    private insert(friend: UserData, arr: UserData[]): void {
        arr.push(friend);

        for (let i = arr.length - 1; i > 0; --i) {
            if (arr[i].displayName < arr[i - 1].displayName) {
                const tmp = arr[i];
                arr[i] = arr[i - 1];
                arr[i - 1] = tmp;
            }
            else {
                break;
            }
        }
    }

    filter(prefix: string): void {
        const online: UserData[] = [];
        const offline: UserData[] = [];

        for (const friend of this.friends) {
            if (!friend.displayName.startsWith(prefix)) {
                continue;
            }
            if (friend.presence === "ONLINE" || friend.presence === "AWAY") {
                online.push(friend);
            }
            else {
                offline.push(friend);
            }
        }

        this._online.set(online);
        this._offline.set(offline);
    }


    refresh(): Observable<FriendList> {
        return this.api.fetchAllMe().pipe(tap(list => {
            const online: UserData[] = [];
            const offline: UserData[] = [];

            this.friends = list.friends;
            for (const friend of list.friends) {
                if (friend.presence === "ONLINE" || friend.presence === "AWAY") {
                    online.push(friend);
                }
                else {
                    offline.push(friend);
                }
            }

            this._online.set(online);
            this._offline.set(offline);
        }));
    }

    unfriend(id: string): Observable<void> {
        const friend = this.friends.find(f => f.id === id);
        if (friend != null) {
            this.friends = this.friends.filter(friend => friend.id !== id);
            this._online.update(f => f.filter(friend => friend.id !== id));
            this._offline.update(f => f.filter(friend => friend.id !== id));
        }

        return this.api.unfriend(id).pipe(tap({
            next: () => {
                if (friend != null) {
                    this.eventService.emit({ type: "unfriend", details: { friend } });
                }
            },

            error: () => {
                if (friend != null) {
                    this.addFriend(friend);
                }
            }
        }));
    }

}