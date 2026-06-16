import { CommonModule } from "@angular/common";
import { Component, input, OnInit } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Presence, UserData } from "../../services/UserApi";
import { SnakeCaseToTitleCasePipe } from "../../pipes/SnakeCaseToTitleCase";

@Component({
    selector: "user",
    templateUrl: "user.component.html",
    styleUrl: "user.component.css",
    imports: [RouterLink, CommonModule, SnakeCaseToTitleCasePipe],
})
export class User implements OnInit {

    user = input.required<UserData>();

    avatarSize = input<number>(30);

    profileLink = input<boolean>(false);

    vertical = input<boolean>(false);

    displayNameSize = input<number>(1);

    showActivity = input<boolean>(false);

    clazz = input<string>("");

    mask1!: string;

    mask2!: string;

    ngOnInit(): void {
        this.mask1 = this.user().id + '-1';
        this.mask2 = this.user().id + '-2';
    }

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