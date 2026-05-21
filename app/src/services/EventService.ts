import { Injectable } from "@angular/core";

export type EventType = "alert";

export type EventListener = (event: ApplicationEvent) => void;

export interface ApplicationEvent {
    type: EventType;
    details: any;
}

export interface AlertEventDetails {
    type: string;
    message: string
}

export type AlertType = "success" | "danger";

export interface AlertEvent extends ApplicationEvent {
    type: "alert";
    details: AlertEventDetails;
}

@Injectable({
    providedIn: 'root'
})
export class EventService {

    private listeners: Map<EventType, EventListener[]> = new Map();

    addEventListener(type: EventType, listener: EventListener): void {
        const listeners = this.listeners.get(type);
        if (!listeners) {
            this.listeners.set(type, [listener]);
        } else {
            listeners.push(listener);
        }
    }

    removeEventListener(type: EventType, listener: EventListener): void {
        const listeners = this.listeners.get(type);
        if (listeners) {
            this.listeners.set(type, listeners.filter(l => l !== listener));
        }
    }

    emit(event: ApplicationEvent): void {
        const listeners = this.listeners.get(event.type);
        if (listeners) {
            listeners.forEach(l => l(event));
        }
    }
}