import { CommonModule } from "@angular/common";
import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Presence, UserData } from "../../api/UserApi";
import { SnakeCaseToTitleCasePipe } from "../../pipes/SnakeCaseToTitleCase";

@Component({
    selector: "user",
    templateUrl: "user.component.html",
    styleUrl: "user.component.css",
    imports: [RouterLink, CommonModule, SnakeCaseToTitleCasePipe],
})
export class User {

    user = input.required<UserData>();

    avatarSize = input<number>(30);

    profileLink = input<boolean>(false);

    vertical = input<boolean>(false);

    displayNameSize = input<number>(1);

    showActivity = input<boolean>(false);

    clazz = input<string>("");

    mask1: number = Math.random() * Number.MAX_SAFE_INTEGER;

    mask2: number = Math.random() * Number.MAX_SAFE_INTEGER;

    getPresenceColor(presence?: Presence): string {
        switch (this.user().presence) {
            case "ONLINE":
                return "green";
            case "AWAY":
                return "orange";
            case "OFFLINE":
                return "dimgray";
            default:
                return "";
        }
    }

}