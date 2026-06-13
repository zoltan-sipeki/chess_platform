import { inject, Injectable } from "@angular/core";
import { AuthService } from "./AuthService";
import { Notification } from "./NotificationApi";
import { UserData } from "./UserApi";

export type RelayEventType = "UNFRIEND" | "NOTIFICATION" | "USER_UPDATED";

export type NotificationRelayEvent = Notification;

export type UserUpdatedRelayEvent = UserData;

export interface UnfriendRelayEvent {
    senderId: string;
}

export interface EventMap {
    "UNFRIEND": UnfriendRelayEvent,
    "NOTIFICATION": NotificationRelayEvent,
    "USER_UPDATED": UserUpdatedRelayEvent
}

@Injectable({
    providedIn: 'root'
})
export class RelayService {

    private ws: MessagePort = new SharedWorker('relay-shared-worker.js').port;

    private authService: AuthService = inject(AuthService);

    private listeners = new Map<RelayEventType, Function[]>();

    constructor() {
        this.ws.onmessage = async e => {
            const { type, payload } = e.data;
            switch (type) {
                case "OPEN":
                    this.ws.postMessage({ type: "AUTHENTICATE", payload: { accessToken: await this.authService.getAccessToken() } });
                    break;
                case "EVENT": {
                    const { type, data } = payload;
                    const listeners = this.listeners.get(type);
                    if (listeners) {
                        for (const listener of listeners) {
                            listener(data);
                        }
                    }
                    break;
                }
            }
        };
    }

    subscribe<T extends RelayEventType>(eventType: T, callback: (event: EventMap[T]) => void): void {
        const listeners = this.listeners.get(eventType);
        if (!listeners) {
            this.listeners.set(eventType, [callback]);
        } else {
            listeners.push(callback);
        }
    }

    unsubscribe(eventType: RelayEventType, callback: Function): void {
        const listeners = this.listeners.get(eventType);
        if (listeners) {
            this.listeners.set(eventType, listeners.filter(l => l !== callback));
        }
    }

}