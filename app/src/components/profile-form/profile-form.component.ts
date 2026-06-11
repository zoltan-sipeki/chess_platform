import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { EventService } from "../../services/EventService";
import { UserService } from "../../services/UserService";
import { AvatarForm } from "../avatar-form/avatar-form.component";

@Component({
    selector: 'profile-form',
    templateUrl: 'profile-form.component.html',
    standalone: true,
    imports: [AvatarForm, ReactiveFormsModule]
})
export class ProfileFormComponent implements OnInit, OnDestroy {

    private userService: UserService = inject(UserService);

    private eventService: EventService = inject(EventService);

    displayName = new FormControl<string | undefined>("");

    ngOnInit(): void {
        this.displayName.setValue(this.userService.currentUser().displayName);
    }

    ngOnDestroy(): void {
    }

    updateDisplayName(): void {
        if (this.displayName.value == null) {
            return;
        }

        this.displayName.disable();
        this.userService.updateDisplayName(this.displayName.value).subscribe({
            next: u => {
                this.displayName.enable();
                this.eventService.emit({ type: "alert", details: { type: "success", message: `Display name updated to "${u.displayName}".` } });
            },
            error: () => {
                this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to update display name. Please try again." } });
            }
        });
    }

}