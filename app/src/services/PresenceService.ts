import { Injectable } from "@angular/core";
import { Presence } from "../api/UserApi";

export interface PresenceConfig {
    name: Presence,
    color: string
}

@Injectable({
    providedIn: "root",
})
export class PresenceService {

    readonly PRESENCES: PresenceConfig[] = [
        { name: "ONLINE", color: "green" },
        { name: "AWAY", color: "orange" },
        { name: "OFFLINE", color: "dimgray" }] as const;
}