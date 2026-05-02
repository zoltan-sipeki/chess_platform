import { Component } from "@angular/core";
import { User } from "../user/user.component";

@Component({
    selector: "user-profile",
    templateUrl: "user-profile.component.html",
    imports: [User]
})
export class UserProfile {

}