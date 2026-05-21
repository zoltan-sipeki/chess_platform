import { Injectable } from "@angular/core";
import { UserData } from "../types";
import { BehaviorSubject, Subscription } from "rxjs";

@Injectable({
    providedIn: "root",
})
export class UserStoreService {

    private user$ = new BehaviorSubject<UserData | null>(null);

    setUser(user: UserData) {
        this.user$.next(user);
    }

    getUser(): UserData | null {
        return this.user$.value;
    }

    subscribe(callback: (user: UserData | null) => void): Subscription {
        return this.user$.subscribe(callback);
    }
}