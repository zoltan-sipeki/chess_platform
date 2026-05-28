import { Injectable } from "@angular/core";
import { BehaviorSubject, Subscription } from "rxjs";
import { UserData } from "../types";

@Injectable({
    providedIn: "root",
})
export class UserStoreService {

    private user$ = new BehaviorSubject<UserData | null>(null);

    setUser(user: UserData) {
        this.user$.next(user);
    }

    subscribe(callback: (user: UserData | null) => void): Subscription {
        return this.user$.subscribe(callback);
    }
}