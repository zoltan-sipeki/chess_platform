import { CommonModule } from "@angular/common";
import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AvatarComponent } from "../avatar/avatar.component";

@Component({
    selector: "user",
    templateUrl: "user.component.html",
    styleUrl: "user.component.css",
    imports: [RouterLink, CommonModule, AvatarComponent]
})
export class User {

    id = input<string>("");

    displayName = input<string>("");

    displayNameSize = input<number>(1);

    avatar = input<string>("");

    avatarSize = input<number>(30);

    profileLink = input<boolean>(false);

    vertical = input<boolean>(false);

}