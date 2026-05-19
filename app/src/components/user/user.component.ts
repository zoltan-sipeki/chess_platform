import { CommonModule } from "@angular/common";
import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: "user",
    templateUrl: "user.component.html",
    styleUrl: "user.component.css",
    imports: [RouterLink, CommonModule]
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