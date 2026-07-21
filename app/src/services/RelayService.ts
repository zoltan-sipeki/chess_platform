import { inject, Injectable } from "@angular/core";
import { Notification } from "../api/NotificationApi";
import { Activity, Presence, UserData } from "../api/UserApi";
import { AuthService } from "./AuthService";
import { CurrentMatch } from "../api/MatchmakingApi";

export type RelayEventType = "UNFRIEND" | "NOTIFICATION" | "USER_UPDATED" | "PRESENCE_CHANGED" | "ACTIVITY_CHANGED" | "MATCH_FOUND" | "REPLAY_READY";

export type NotificationRelayEvent = Notification;

export type MatchFoundRelayEvent = CurrentMatch;

export type UserUpdatedRelayEvent = UserData;

export interface UnfriendRelayEvent {
    senderId: string;
}

export interface PresenceChangedRelayEvent {
    userId: string;
    presence: Presence;
}

export interface ActivityChangedRelayEvent {
    userId: string;
    activity: Activity;
}

export interface ReplayReadyRelayEvent {
    replayId: string;
}

export interface EventMap {
    "UNFRIEND": UnfriendRelayEvent,
    "NOTIFICATION": NotificationRelayEvent,
    "USER_UPDATED": UserUpdatedRelayEvent,
    "PRESENCE_CHANGED": PresenceChangedRelayEvent,
    "ACTIVITY_CHANGED": ActivityChangedRelayEvent,
    "MATCH_FOUND": MatchFoundRelayEvent,
    "REPLAY_READY": ReplayReadyRelayEvent
}

@Injectable({
    providedIn: 'root'
})
export class RelayService {

    private ws: MessagePort = new SharedWorker('relay-shared-worker.js').port;

    private authService: AuthService = inject(AuthService);

    private listeners = new Map<RelayEventType, Function[]>();

    private latestEvents = new Map<RelayEventType, EventMap[RelayEventType]>();

    constructor() {
        this.ws.onmessage = async e => {
            const { type, payload } = e.data;
            console.log(e.data);
            switch (type) {
                case "OPEN": {
                    const accessToken = await this.authService.getAccessToken();
                    this.ws.postMessage({ type: "AUTHENTICATE", payload: { accessToken } });
                    break;
                }
                case "EVENT": {
                    const { type, data } = payload;
                    this.latestEvents.set(type, data);
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

        const latestEvent = this.latestEvents.get(eventType) as EventMap[T];
        if (latestEvent != null) {
            callback(latestEvent);
        }
    }

    unsubscribe(eventType: RelayEventType, callback: Function): void {
        const listeners = this.listeners.get(eventType);
        if (listeners) {
            this.listeners.set(eventType, listeners.filter(l => l !== callback));
        }
    }

}