import { inject, Injectable, Signal, signal, WritableSignal } from "@angular/core";
import { Observable } from "rxjs";
import { tap } from 'rxjs/operators';
import { Notification, NotificationApi, NotificationList, NotificationQuery, NotificationUpdate } from "./NotificationApi";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    private api: NotificationApi = inject(NotificationApi);

    private _notifications = signal<NotificationList>({ unread: 0, lastReadSeq: 0, notifications: [], last: -1 });

    readonly notifications: Signal<NotificationList> = this._notifications.asReadonly();

    private friendRequestState = new FriendRequestState();

    getAccepting(id: string): Signal<boolean> | undefined {
        return this.friendRequestState.getAccepting(id);
    }

    getRejecting(id: string): Signal<boolean> | undefined {
        return this.friendRequestState.getRejecting(id);
    }

    setAccepting(id: string, value: boolean): void {
        this.friendRequestState.setAccepting(id, value);
    }

    setRejecting(id: string, value: boolean): void {
        this.friendRequestState.setRejecting(id, value);
    }

    fetchAll(query: NotificationQuery): Observable<NotificationList> {
        return this.api.fetchAll(query);
    }

    refresh(limit: number = 5): Observable<NotificationList> {
        return this.api.fetchAll({ limit }).pipe(tap(result => {
            this._notifications.set(result);
            this.friendRequestState.add(result.notifications);
        }));
    }

    trim(): void {
        this._notifications.update(l => {
            const arr = l.notifications.slice(0, 5);
            this.friendRequestState.clear();
            this.friendRequestState.add(arr);
            return {
                ...l,
                notifications: arr
            };
        });
    }


    loadMore(max?: number): Observable<NotificationList> {
        let limit = 0;
        if (max == null) {
            limit = 5;
        }
        else if (max > this._notifications().notifications.length) {
            limit = max - this._notifications().notifications.length;
        }
        else {
            return new Observable<NotificationList>(observer => observer.next(this._notifications()));
        }

        const last = this._notifications().last;
        if (last == null) {
            return new Observable<NotificationList>(observer => observer.next(this._notifications()));
        }

        return this.api.fetchAll({ before: last, limit }).pipe(tap(result => {
            this._notifications.update(l => ({
                ...l,
                notifications: [...l.notifications, ...result.notifications],
                last: result.last
            }));
            this.friendRequestState.add(result.notifications);
        }));
    }

    updateUnreadCount(count: number): void {
        this._notifications.update(l => ({ ...l, unread: count }));
    }

    updateAll(update: NotificationUpdate): Observable<void> {
        return this.api.updateAll(update);
    }

    delete(id: string): Observable<void> {
        const deleted = this._notifications().notifications.find((notification: Notification) => notification.id === id);
        if (deleted != null) {
            this._notifications.update(list => ({
                ...list,
                notifications: list.notifications.filter((notification: Notification) => notification.id !== id)
            }));
            this.friendRequestState.remove(id);
        };


        return this.api.delete(id).pipe(tap({
            error: () => {
                if (deleted == null) {
                    return;
                }

                this.friendRequestState.add(deleted.id);

                const arr = this._notifications().notifications;
                arr.push(deleted);
                for (let i = arr.length - 1; i >= 1; --i) {
                    if (arr[i].seq > arr[i - 1].seq) {
                        const tmp = arr[i];
                        arr[i] = arr[i - 1];
                        arr[i - 1] = tmp;
                    }
                    else {
                        break;
                    }
                }
            }
        }));
    }

    deleteAll(): Observable<void> {
        const list = this._notifications().notifications;
        this._notifications.update(list => ({ ...list, notifications: [] }));
        this.friendRequestState.clear();

        return this.api.deleteAll().pipe(tap({
            error: () => {
                this._notifications.update(l => ({ ...l, notifications: list }));
                this.friendRequestState.add(list);
            }
        }));
    }

    deleteByFriendRequestId(id: string): void {
        this.friendRequestState.remove(id);
        this._notifications.update(list => {
            return {
                ...list,
                notifications: list.notifications.filter(notification => notification.friendRequest !== id),
            }
        });
    }
}

class FriendRequestState {

    private accepting = new Map<string, WritableSignal<boolean>>();

    private rejecting = new Map<string, WritableSignal<boolean>>();

    clear(): void {
        this.accepting.clear();
        this.rejecting.clear();
    }

    remove(id: string): void {
        this.accepting.delete(id);
        this.rejecting.delete(id);
    }

    add(id: string | Notification[]): void {
        if (typeof id === "string") {
            this.accepting.set(id, signal<boolean>(false));
            this.rejecting.set(id, signal<boolean>(false));
        }
        else {
            for (const i of id) {
                this.accepting.set(i.id, signal<boolean>(false));
                this.rejecting.set(i.id, signal<boolean>(false));
            }
        }
    }

    setAccepting(id: string, value: boolean): void {
        this.accepting.get(id)?.set(value);
    }

    setRejecting(id: string, value: boolean): void {
        this.rejecting.get(id)?.set(value);
    }

    getAccepting(id: string): Signal<boolean> | undefined {
        return this.accepting.get(id);
    }

    getRejecting(id: string): Signal<boolean> | undefined {
        return this.rejecting.get(id);
    }
}

