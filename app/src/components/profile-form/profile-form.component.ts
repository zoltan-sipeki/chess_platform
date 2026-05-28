import { Component, inject, OnDestroy, OnInit } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { Subscription } from "rxjs";
import { UserService } from "../../services/UserService";
import { UserStore } from "../../services/UserStore";
import { AvatarForm } from "../avatar-form/avatar-form.component";

@Component({
    selector: 'profile-form',
    templateUrl: 'profile-form.component.html',
    standalone: true,
    imports: [AvatarForm, ReactiveFormsModule]
})
export class ProfileFormComponent implements OnInit, OnDestroy {

    private userStore: UserStore = inject(UserStore);

    private userService: UserService = inject(UserService);

    private userStoreSub?: Subscription;

    displayName = new FormControl<string | undefined>("");

    ngOnInit(): void {
        this.userStoreSub = this.userStore.subscribe(user => {
            this.displayName.setValue(user?.displayName);
        });
    }

    ngOnDestroy(): void {
        this.userStoreSub?.unsubscribe();
    }

    updateDisplayName(): void {
        if (this.displayName.value == null) {
            return;
        }

        this.displayName.disable();
        this.userService.updateDisplayName(this.displayName.value).subscribe(u => {
            this.userStore.setUser(u);
            this.displayName.enable();
        });
    }

}