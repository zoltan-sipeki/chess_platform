import { Injectable } from "@angular/core";
import { BehaviorSubject, Subscription } from "rxjs";
import { UserData } from "../types";

@Injectable({
    providedIn: "root",
})
export class UserStore {

    private user$ = new BehaviorSubject<UserData>({ id: "", displayName: "", avatar: "" });

    setUser(user: UserData) {
        this.user$.next(user);
    }

    subscribe(callback: (user: UserData) => void): Subscription {
        return this.user$.subscribe(callback);
    }
}