import { Component, input } from "@angular/core";

@Component({
    selector: 'avatar',
    templateUrl: './avatar.component.html',
})
export class AvatarComponent {

    src = input<string>("");

    width = input<number>(0);

    height = input<number>(0);

    clazz = input<string>("");
}