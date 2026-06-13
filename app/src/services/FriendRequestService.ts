import { inject, Injectable, Signal, signal } from "@angular/core";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { UserData } from "../types";
import { EventService } from "./EventService";
import { FriendRequest, FriendRequestApi } from './FriendRequestApi';
import { FriendService } from "./FriendService";
import { NotificationService } from "./NotificationService";
import { RelayService } from "./RelayService";

@Injectable({
    providedIn: "root",
})
export class FriendRequestService {

    private api: FriendRequestApi = inject(FriendRequestApi);

    private notificationService: NotificationService = inject(NotificationService);

    private relayService: RelayService = inject(RelayService);

    private friendService: FriendService = inject(FriendService);

    private eventService: EventService = inject(EventService);

    private _friendRequests = signal<FriendRequest[]>([]);

    readonly friendRequests: Signal<FriendRequest[]> = this._friendRequests.asReadonly();


    constructor() {
        this.relayService.subscribe("NOTIFICATION", n => {
            if (n.type === "FRIEND_REQUEST") {
                this._friendRequests.update(requests => [{ id: n.friendRequest!, sender: n.sender }, ...requests]);
            }
        });
    }

    fetchAll(): Observable<FriendRequest[]> {
        return this.api.fetchAll();
    }

    refresh(): Observable<FriendRequest[]> {
        return this.api.fetchAll().pipe(tap(result => {
            this._friendRequests.set(result);
        }));
    }

    sendRequest(userId: string): Observable<UserData | null> {
        return this.api.sendRequest(userId).pipe(tap(friend => {
            console.log(friend);
            if (friend != null) {
                this.friendService.addFriend(friend);
                this.notificationService.deleteBy({ senderId: friend.id, type: "FRIEND_REQUEST" });
                this._friendRequests.update(requests => requests.filter(request => request.sender.id !== friend.id));
            }
        }));
    }

    acceptRequest(id: string): Observable<UserData | null> {
        const request = this._friendRequests().find(request => request.id === id);
        if (request != null) {
            this._friendRequests.update(requests => requests.filter(request => request.id !== id));
        }

        return this.api.updateRequest(id, { status: "ACCEPTED" }).pipe(tap({
            next: (friend) => {
                this.notificationService.deleteBy({ friendRequest: id });
                if (friend != null) {
                    this.friendService.addFriend(friend);
                    this.eventService.emit({ type: "friend-request-accepted", details: { friend } });
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `${friend.displayName} and you are now friends.` } });
                }
            },
            error: () => {
                if (request != null) {
                    this._friendRequests.update(requests => [...requests, request]);
                }
            }
        }));
    }

    rejectRequest(id: string): Observable<UserData | null> {
        const request = this._friendRequests().find(request => request.id === id);
        if (request != null) {
            this._friendRequests.update(requests => requests.filter(request => request.id !== id));
        }

        return this.api.updateRequest(id, { status: "REJECTED" }).pipe(tap({
            next: () => {
                this.notificationService.deleteBy({ friendRequest: id });
            },
            error: () => {
                if (request != null) {
                    this._friendRequests.update(requests => [...requests, request]);
                }
            }
        }));
    }
}

