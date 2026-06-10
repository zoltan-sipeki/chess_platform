import { inject, Injectable, Signal, signal } from "@angular/core";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { UserData } from "../types";
import { EventService } from "./EventService";
import { FriendRequest, FriendRequestApi } from './FriendRequestApi';
import { NotificationService } from "./NotificationService";

@Injectable({
    providedIn: "root",
})
export class FriendRequestService {

    private api: FriendRequestApi = inject(FriendRequestApi);

    private notificationService: NotificationService = inject(NotificationService);

    private eventService: EventService = inject(EventService);

    private _friendRequests = signal<FriendRequest[]>([]);

    readonly friendRequests: Signal<FriendRequest[]> = this._friendRequests.asReadonly();

    fetchAll(): Observable<FriendRequest[]> {
        return this.api.fetchAll();
    }

    refresh(): Observable<FriendRequest[]> {
        return this.api.fetchAll().pipe(tap(result => {
            this._friendRequests.set(result);
        }));
    }

    acceptFriendRequest(id: string): Observable<UserData | null> {
        const request = this._friendRequests().find(request => request.id === id);
        if (request != null) {
            this._friendRequests.update(requests => requests.filter(request => request.id !== id));
        }

        return this.api.updateRequest(id, { status: "ACCEPTED" }).pipe(tap({
            next: (friend) => {
                this.notificationService.deleteByFriendRequestId(id);
                if (friend != null) {
                    this.eventService.emit({ type: "friend-request-accepted", details: { friend } });
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `${friend.displayName} and you are now friends.` } });
                }
            },
            error: () => {
                if (request != null) {
                    this._friendRequests.update(requests => {
                        requests.push(request);
                        return requests;
                    });
                }
            }
        }));
    }

    rejectFriendRequest(id: string): Observable<UserData | null> {
        const request = this._friendRequests().find(request => request.id === id);
        if (request != null) {
            this._friendRequests.update(requests => requests.filter(request => request.id !== id));
        }

        return this.api.updateRequest(id, { status: "REJECTED" }).pipe(tap({
            next: () => {
                this.notificationService.deleteByFriendRequestId(id);
            },
            error: () => {
                if (request != null) {
                    this._friendRequests.update(requests => {
                        requests.push(request);
                        return requests;
                    });
                }
            }
        }));
    }
}

