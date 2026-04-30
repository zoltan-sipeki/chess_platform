import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
    selector: "user",
    templateUrl: "user.component.html",
    imports: [RouterLink]
})
export class User {

    id = input<string>("");

    displayName = input<string>("");

    avatar = input<string>("");

}