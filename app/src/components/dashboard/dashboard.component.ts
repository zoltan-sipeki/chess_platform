import { Component } from "@angular/core";
import { Navbar } from "../navbar/navbar.component";

@Component({
    selector: "dashboard",
    templateUrl: "./dashboard.component.html",
    standalone: true,
    imports: [Navbar]
})
export class Dashboard {

}