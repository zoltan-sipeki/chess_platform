import { inject, Injectable, Signal, signal } from "@angular/core";
import { Observable, tap } from "rxjs";
import { Avatar, AvatarApi } from "./AvatarApi";
import { UserApi, UserData } from "./UserApi";

@Injectable({
    providedIn: "root",
})
export class UserService {

    private userApi: UserApi = inject(UserApi);

    private avatarApi: AvatarApi = inject(AvatarApi);

    private _currentUser = signal<UserData>({
        id: "",
        displayName: "",
        avatar: "",
    });

    readonly currentUser: Signal<UserData> = this._currentUser.asReadonly();

    refresh(): Observable<UserData> {
        return this.userApi.fetchMe().pipe(tap(user => {
            this._currentUser.set(user);
        }));
    }

    updateDisplayName(displayName: string): Observable<UserData> {
        return this.userApi.updateDisplayName(displayName).pipe(tap(user => {
            this._currentUser.update(u => {
                u.displayName = displayName;
                return u;
            })
        }));
    }

    uploadAvatar(avatar: Blob): Observable<Avatar> {
        return this.avatarApi.upload(avatar).pipe(tap(avatar => {
            this._currentUser.update(u => {
                u.avatar = avatar.id;
                return u;
            });
        }));
    }

    deleteAvatar(): Observable<Avatar> {
        return this.avatarApi.delete().pipe(tap(avatar => {
            this._currentUser.update(u => {
                u.avatar = avatar.id;
                return u;
            });
        }));
    }
}