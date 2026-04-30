import { Component, input } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: "play-menu",
    templateUrl: "play-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem]
})
export class PlayMenu {

    clazz = input<string>("");
}