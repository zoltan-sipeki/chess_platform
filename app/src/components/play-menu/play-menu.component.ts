import { Component, inject, input } from "@angular/core";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from "@ng-bootstrap/ng-bootstrap";
import { MatchmakingService } from "../../services/MatchmakingService";
import { QueueType } from "../../api/MatchmakingApi";

@Component({
    selector: "play-menu",
    templateUrl: "play-menu.component.html",
    imports: [NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem]
})
export class PlayMenu {
    
    private matchmakingService: MatchmakingService = inject(MatchmakingService);

    clazz = input<string>("");

    enqueue(queueType: QueueType): void {
        this.matchmakingService.enqueue(queueType).subscribe();
    }
}