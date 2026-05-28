import { Component, ElementRef, inject, OnDestroy, OnInit, signal, viewChild } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { Subscription } from "rxjs";
import { AvatarService } from "../../services/AvatarService";
import { EventService } from "../../services/EventService";
import { UserStore } from "../../services/UserStore";
import { AvatarEditor } from "../avatar-editor/avatar-editor.component";
import { AvatarComponent } from "../avatar/avatar.component";

@Component({
    selector: 'avatar-form',
    templateUrl: 'avatar-form.component.html',
    standalone: true,
    imports: [AvatarComponent]
})
export class AvatarForm implements OnInit, OnDestroy {

    private modalService: NgbModal = inject(NgbModal);

    private avatarService: AvatarService = inject(AvatarService);

    private userStore: UserStore = inject(UserStore);

    private eventService: EventService = inject(EventService);

    private userStoreSub?: Subscription;

    private fileInput = viewChild<ElementRef<HTMLInputElement>>('fileInput');

    private img?: HTMLImageElement;

    removing = signal<boolean>(false);

    uploading = signal<boolean>(false);

    avatar = signal<string>("");

    ngOnInit(): void {
        this.userStoreSub = this.userStore.subscribe(user => {
            if (user != null) {
                this.avatar.set(user.avatar);
            }
        });
    }

    ngOnDestroy(): void {
        if (this.img?.src != null) {
            URL.revokeObjectURL(this.img?.src);
        }

        this.userStoreSub?.unsubscribe();
    }


    selectImage(): void {
        this.fileInput()?.nativeElement.click();
    }

    async onImageSelect(): Promise<void> {
        const img = this.fileInput()?.nativeElement.files![0] as Blob;

        this.img = new Image();
        this.img.src = URL.createObjectURL(img);
        await this.img.decode();

        const modalRef = this.modalService.open(AvatarEditor);
        modalRef.componentInstance.img = this.img;
        modalRef.closed.subscribe((blob) => {
            this.uploading.set(true);
            this.avatarService.upload(blob).subscribe({
                next: res => {
                    this.avatar.set(res.id);
                    this.uploading.set(false);
                    this.eventService.emit({ type: "alert", details: { type: "success", message: "Avatar uploaded successfully." } });
                },
                error: () => {
                    this.uploading.set(false);
                    this.eventService.emit({ type: "alert", details: { type: "error", message: "Failed to upload avatar. Please try again." } });
                }
            });
        });
    }

    deleteAvatar(): void {
        this.removing.set(true);
        this.avatarService.delete().subscribe(res => {
            this.avatar.set(res.id);
            this.removing.set(false);
            this.eventService.emit({ type: "alert", details: { type: "success", message: "Avatar deleted successfully." } });
        });
    }
}