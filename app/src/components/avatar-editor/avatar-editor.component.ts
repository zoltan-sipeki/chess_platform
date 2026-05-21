import { AfterViewInit, Component, ElementRef, inject, input, OnDestroy, OnInit, viewChild } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { Subscription } from "rxjs";

interface Point {
    x: number;
    y: number;
}

interface Rect {
    top: number;
    left: number;
    right: number;
    bottom: number;
}

@Component({
    selector: 'avatar-editor',
    templateUrl: 'avatar-editor.component.html',
    imports: [ReactiveFormsModule],
    standalone: true
})
export class AvatarEditor implements OnInit, OnDestroy, AfterViewInit {

    private modal: NgbActiveModal = inject(NgbActiveModal);

    readonly AVATAR_SIZE: number = 255;

    readonly CANVAS_HEIGHT: number = this.AVATAR_SIZE;

    readonly CANVAS_WIDTH: number = (this.CANVAS_HEIGHT * 16) / 9;

    private readonly SHRINK_FACTOR: number = 0.95;

    private readonly MAGNIFIY_FACTOR: number = 1.05;

    private mainCanvas = viewChild<ElementRef<HTMLCanvasElement>>('mainCanvas');

    private offCanvas = viewChild<ElementRef<HTMLCanvasElement>>('offScreenCanvas');

    private mainContext!: CanvasRenderingContext2D;

    private offContext!: CanvasRenderingContext2D;

    private prevX: number = 0;

    private prevY: number = 0;

    rangeScale = new FormControl(1, { nonNullable: true });

    private rangeScaleSub?: Subscription;

    private prevRangeScale = this.rangeScale.value;

    img = new Image();

    close(): void {
        this.modal.dismiss();
    }

    ngOnInit(): void {
        this.rangeScaleSub = this.rangeScale.valueChanges.subscribe((value) => this.rangeInputScale(value));
    }

    ngOnDestroy(): void {
        this.rangeScaleSub?.unsubscribe();
    }

    ngAfterViewInit(): void {
        this.mainContext = this.mainCanvas()?.nativeElement.getContext("2d")!;
        this.offContext = this.offCanvas()?.nativeElement.getContext("2d")!;
        this.loadImage();
    }

    private drawImage(): void {
        this.mainContext.save();
        this.mainContext.globalCompositeOperation = "destination-over";
        this.mainContext.drawImage(this.img, 0, 0);
        this.drawBackground();
        this.mainContext.restore();
    }

    private drawBackground(): void {
        this.mainContext.save();
        this.mainContext.resetTransform();
        this.mainContext.fillRect(0, 0, this.CANVAS_WIDTH, this.CANVAS_HEIGHT);
        this.mainContext.restore();
    }

    private clearCanvas(): void {
        this.mainContext.save();
        this.mainContext.resetTransform();
        this.mainContext.clearRect(0, 0, this.CANVAS_WIDTH, this.CANVAS_HEIGHT);
        this.mainContext.restore();
    }

    private drawOverlay(): void {
        this.mainContext.save();
        this.mainContext.resetTransform();
        this.mainContext.fillStyle = "rgba(50, 50, 50, 0.7)";
        this.mainContext.fillRect(0, 0, this.CANVAS_WIDTH, this.CANVAS_HEIGHT);
        this.mainContext.globalCompositeOperation = "destination-out";
        this.mainContext.beginPath();
        this.mainContext.fillStyle = "#000000";
        this.mainContext.arc(
            this.CANVAS_WIDTH / 2,
            this.CANVAS_HEIGHT / 2,
            this.AVATAR_SIZE / 2,
            0,
            Math.PI * 2
        );
        this.mainContext.fill();
        this.mainContext.globalCompositeOperation = "source-over";
        this.mainContext.beginPath();
        this.mainContext.strokeStyle = "#ffffff";
        this.mainContext.lineWidth = 3;
        this.mainContext.arc(
            this.CANVAS_WIDTH / 2,
            this.CANVAS_HEIGHT / 2,
            this.AVATAR_SIZE / 2,
            0,
            Math.PI * 2
        );
        this.mainContext.stroke();
        this.mainContext.restore();
    }

    private drawCanvas(): void {
        this.clearCanvas();
        this.drawOverlay();
        this.drawImage();
    }

    private loadImage(): void {
        this.mainContext.resetTransform();

        let scaleFactor = this.CANVAS_HEIGHT / this.img.height;
        const scaledWidth = scaleFactor * this.img.width;

        if (scaledWidth < this.AVATAR_SIZE) {
            scaleFactor *= this.AVATAR_SIZE / scaledWidth;
        }

        const xMiddle = (this.img.width * scaleFactor) / 2;
        const yMiddle = (this.img.height * scaleFactor) / 2;

        this.mainContext.translate(
            this.CANVAS_WIDTH / 2 - xMiddle,
            this.CANVAS_HEIGHT / 2 - yMiddle
        );

        this.mainContext.scale(scaleFactor, scaleFactor);

        this.drawCanvas();
    }

    private multiplyByMatrix(matrix: DOMMatrix, point: Point): Point {
        const x1 = point.x * matrix.a + point.y * matrix.c;
        const y1 = point.x * matrix.b + point.y * matrix.d;

        return { x: x1, y: y1 };
    }

    private translateImage(delta: Point): void {
        const offset = this.multiplyByMatrix(
            this.mainContext.getTransform().inverse(),
            delta
        );

        this.mainContext.translate(offset.x, offset.y);
    }

    private scaleImage(scaleFactor: number, origin: Point): void {
        const transform = this.mainContext.getTransform();
        const delta = { x: origin.x - transform.e, y: origin.y - transform.f };
        const offset = this.multiplyByMatrix(transform.inverse(), delta);

        this.mainContext.translate(offset.x, offset.y);
        this.mainContext.scale(scaleFactor, scaleFactor);
        this.mainContext.translate(-offset.x, -offset.y);
    }

    private getCanvasPosition(): Point {
        const rect = this.mainCanvas()?.nativeElement.getBoundingClientRect()!;
        return { x: rect.left, y: rect.top };
    }

    private getAvatarBoundingRect(): Rect {
        const top = 0;
        const left = Math.floor((this.CANVAS_WIDTH - this.AVATAR_SIZE) / 2);
        const right = left + this.AVATAR_SIZE;
        const bottom = this.AVATAR_SIZE;

        return { top, left, right, bottom };
    }

    private getImageBoundingRect(): Rect {
        const transform = this.mainContext.getTransform();
        const dimension = this.multiplyByMatrix(
            transform,
            { x: this.img.width, y: this.img.height }
        );
        const top = transform.f;
        const left = transform.e;
        const right = left + dimension.x;
        const bottom = top + dimension.y;

        return { top, left, right, bottom };
    }

    private correctImgPosition(img: Rect, avatar: Rect): void {
        const delta = {
            x: 0,
            y: 0
        };

        if (img.top >= avatar.top) {
            delta.y += avatar.top - img.top;
        }
        if (img.left >= avatar.left) {
            delta.x += avatar.left - img.left;
        }
        if (img.right <= avatar.right) {
            delta.x += avatar.right - img.right;
        }
        if (img.bottom <= avatar.bottom) {
            delta.y += avatar.bottom - img.bottom;
        }

        this.translateImage(delta);
    }

    private shrinkImage(origin: Point, times: number = 1): void {
        let img = this.getImageBoundingRect();
        let imgHeight = img.bottom - img.top;
        let imgWidth = img.right - img.left;

        if (
            Math.round(imgHeight) === this.AVATAR_SIZE ||
            Math.round(imgWidth) === this.AVATAR_SIZE
        ) {
            return;
        }

        this.scaleImage(this.SHRINK_FACTOR ** times, origin);

        img = this.getImageBoundingRect();
        imgHeight = img.bottom - img.top;
        imgWidth = img.right - img.left;

        let scaleFactor = 1;

        if (imgHeight < this.AVATAR_SIZE) {
            scaleFactor *= this.AVATAR_SIZE / imgHeight;
        }

        if (imgWidth < this.AVATAR_SIZE) {
            scaleFactor *= this.AVATAR_SIZE / imgWidth;
        }

        this.mainContext.scale(scaleFactor, scaleFactor);
        this.correctImgPosition(this.getImageBoundingRect(), this.getAvatarBoundingRect());
    }

    private magnifyImage(origin: Point, times: number = 1): void {
        this.scaleImage(this.MAGNIFIY_FACTOR ** times, origin);
        this.correctImgPosition(this.getImageBoundingRect(), this.getAvatarBoundingRect());
    }

    private drawOnOffScreenCanvas(): void {
        const transform = this.mainContext.getTransform();
        this.offContext.setTransform(this.mainContext.getTransform());

        const offset = this.multiplyByMatrix(transform.inverse(), { x: this.CANVAS_WIDTH / 2 - this.AVATAR_SIZE / 2, y: 0 });
        this.offContext.translate(-offset.x, -offset.y);
        this.offContext.drawImage(this.img, 0, 0);
    }

    submit(): void {
        this.drawOnOffScreenCanvas();
        this.offCanvas()?.nativeElement.toBlob((blob) => this.modal.close(blob));
    }

    startDragging(e: MouseEvent): void {
        this.prevX = e.clientX;
        this.prevY = e.clientY;
        document.addEventListener("mouseup", this.cancelDragging);
        document.addEventListener("mousemove", this.moveImage);
    }

    cancelDragging = (e: MouseEvent): void => {
        document.removeEventListener("mouseup", this.cancelDragging);
        document.removeEventListener("mousemove", this.moveImage);
    }

    moveImage = (e: MouseEvent): void => {
        const delta = { x: e.clientX - this.prevX, y: e.clientY - this.prevY };

        this.translateImage(delta);

        this.correctImgPosition(this.getImageBoundingRect(), this.getAvatarBoundingRect());

        this.drawCanvas();
        this.prevX = e.clientX;
        this.prevY = e.clientY;
    }

    wheelScale(e: WheelEvent): void {
        e.preventDefault();
        const canvas = this.getCanvasPosition();
        const origin = { x: e.clientX - canvas.x, y: e.clientY - canvas.y };

        let change = 0;

        if (e.deltaY > 0) {
            if (this.rangeScale.value > 1) {
                this.shrinkImage(origin);
                --change;
            }
        } else {
            if (this.rangeScale.value < 100) {
                this.magnifyImage(origin);
                ++change;
            }
        }

        this.rangeScale.setValue(this.rangeScale.value + change);
        this.prevRangeScale = this.rangeScale.value;

        this.drawCanvas();
    }

    rangeInputScale(scale: number): void {
        const diff = scale - this.prevRangeScale;

        const origin = { x: this.CANVAS_WIDTH / 2, y: this.CANVAS_HEIGHT / 2 };

        if (diff < 0) {
            this.shrinkImage(origin, Math.abs(diff));
        }
        else {
            this.magnifyImage(origin, diff);
        }

        this.drawCanvas();

        this.prevRangeScale = scale;
    }
}