import { Injectable } from "@angular/core";
import { UserData } from "../api/UserApi";

export type EventType = "alert" | "friend-request-accepted" | "unfriend";

export interface EventMap {
    "alert": AlertEvent;
    "friend-request-accepted": FriendRequestAcceptedEvent;
    "unfriend": UnfriendEvent;
}

export type AlertType = "success" | "danger";

export interface AlertEvent {
    type: "alert";
    details: {
        type: AlertType;
        message: string
    };
}

export interface FriendRequestAcceptedEvent {
    type: "friend-request-accepted";
    details: {
        friend: UserData
    };
}

export interface UnfriendEvent {
    type: "unfriend";
    details: {
        friend: UserData
    };
}

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private listeners: Map<EventType, Function[]> = new Map();

    addEventListener<T extends EventType>(type: T, listener: (event: EventMap[T]) => void): void {
        const listeners = this.listeners.get(type);
        if (!listeners) {
            this.listeners.set(type, [listener]);
        } else {
            listeners.push(listener);
        }
    }

    removeEventListener<T extends EventType>(type: T, listener: (event: EventMap[T]) => void): void {
        const listeners = this.listeners.get(type);
        if (listeners) {
            this.listeners.set(type, listeners.filter(l => l !== listener));
        }
    }

    emit<T extends EventType>(event: EventMap[T]): void {
        const listeners = this.listeners.get(event.type);
        if (listeners) {
            listeners.forEach(l => l(event));
        }
    }
}