import { AsyncPipe } from "@angular/common";
import { Component, inject, OnInit } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Observable } from "rxjs";
import { EventService } from "../../services/EventService";
import { PrivacyApi, PrivacySetting, PrivacySettings } from "../../services/PrivacyApi";

@Component({
    selector: "privacy-form",
    templateUrl: "privacy-form.component.html",
    standalone: true,
    imports: [AsyncPipe, ReactiveFormsModule]
})
export class PrivacyForm implements OnInit {

    private privacyApi: PrivacyApi = inject(PrivacyApi);

    private eventService: EventService = inject(EventService);

    privacy$?: Observable<PrivacySettings>;

    form = new FormGroup({
        friends: new FormControl(""),
        playerStats: new FormControl(""),
        matchStats: new FormControl(""),
        matchHistory: new FormControl(""),
    });

    ngOnInit(): void {
        this.privacy$ = this.privacyApi.fetch();
        this.privacy$.subscribe(privacy => {
            this.form.patchValue(privacy, { emitEvent: false });
        });

        this.form.get("friends")?.valueChanges.subscribe(val => {
            this.form.disable({ emitEvent: false });
            this.privacyApi.updateChatPrivacy({ friends: val as PrivacySetting }).subscribe({
                next: () => {
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `Your friend list ${this.translate(val as PrivacySetting)}` } });
                    this.form.enable({ emitEvent: false });
                },
                error: () => {
                    this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to update privacy settings. Please try again." } });
                    this.form.enable({ emitEvent: false });
                }
            })
        });

        this.form.get("playerStats")?.valueChanges.subscribe(val => {
            this.form.disable({ emitEvent: false });
            this.privacyApi.updateMatchPrivacy({ playerStats: val as PrivacySetting }).subscribe({
                next: () => {
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `Your player stats ${this.translate(val as PrivacySetting)}` } });
                    this.form.enable({ emitEvent: false });
                },
                error: () => {
                    this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to update privacy settings. Please try again." } });
                    this.form.enable({ emitEvent: false });
                }
            });
        });

        this.form.get("matchStats")?.valueChanges.subscribe(val => {
            this.form.disable({ emitEvent: false });
            this.privacyApi.updateMatchPrivacy({ matchStats: val as PrivacySetting }).subscribe({
                next: () => {
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `Your match stats ${this.translate(val as PrivacySetting)}` } });
                    this.form.enable({ emitEvent: false });
                },
                error: () => {
                    this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to update privacy settings. Please try again." } });
                    this.form.enable({ emitEvent: false });
                }
            });
        });

        this.form.get("matchHistory")?.valueChanges.subscribe(val => {
            this.form.disable({ emitEvent: false });
            this.privacyApi.updateMatchPrivacy({ matchHistory: val as PrivacySetting }).subscribe({
                next: () => {
                    this.eventService.emit({ type: "alert", details: { type: "success", message: `Your match history ${this.translate(val as PrivacySetting)}` } });
                    this.form.enable({ emitEvent: false });
                },
                error: () => {
                    this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to update privacy settings. Please try again." } });
                    this.form.enable({ emitEvent: false });
                }
            });
        });
    }

    private translate(setting: PrivacySetting): string {
        switch (setting) {
            case "PUBLIC":
                return "is/are now Public.";
            case "FRIENDS":
                return "can now be seen by your friends only.";
            case "PRIVATE":
                return "is/are not Private";
        }
    }
}