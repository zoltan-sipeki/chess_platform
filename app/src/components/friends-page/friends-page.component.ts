import { Component } from "@angular/core";
import { User } from "../user/user.component";

@Component({
    selector: 'friends-page',
    templateUrl: 'friends-page.component.html',
    imports: [User]
})
export class FriendsPage {

}