import { Component, ElementRef, inject, OnDestroy, OnInit, Signal, signal, viewChild } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { EventService } from "../../services/EventService";
import { UserService } from "../../services/UserService";
import { AvatarEditor } from "../avatar-editor/avatar-editor.component";
import { AvatarComponent } from "../avatar/avatar.component";
import { UserData } from "../../services/UserApi";

@Component({
    selector: 'avatar-form',
    templateUrl: 'avatar-form.component.html',
    standalone: true,
    imports: [AvatarComponent]
})
export class AvatarForm implements OnInit, OnDestroy {

    private modalService: NgbModal = inject(NgbModal);

    private userService: UserService = inject(UserService);

    private eventService: EventService = inject(EventService);

    private fileInput = viewChild<ElementRef<HTMLInputElement>>('fileInput');

    private img?: HTMLImageElement;

    removing = signal<boolean>(false);

    uploading = signal<boolean>(false);

    currentUser!: Signal<UserData>;

    ngOnInit(): void {
        this.currentUser = this.userService.currentUser;
    }

    ngOnDestroy(): void {
        if (this.img?.src != null) {
            URL.revokeObjectURL(this.img?.src);
        }
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
            this.userService.uploadAvatar(blob).subscribe({
                next: res => {
                    this.uploading.set(false);
                    this.eventService.emit({ type: "alert", details: { type: "success", message: "Avatar uploaded successfully." } });
                },
                error: () => {
                    this.uploading.set(false);
                    this.eventService.emit({ type: "alert", details: { type: "danger", message: "Failed to upload avatar. Please try again." } });
                }
            });
        });
    }

    deleteAvatar(): void {
        this.removing.set(true);
        this.userService.deleteAvatar().subscribe(res => {
            this.removing.set(false);
            this.eventService.emit({ type: "alert", details: { type: "success", message: "Avatar deleted successfully." } });
        });
    }
}