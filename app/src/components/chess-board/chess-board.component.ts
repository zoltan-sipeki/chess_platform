import { AfterViewInit, Component, effect, ElementRef, input, output, viewChild, viewChildren } from "@angular/core";
import { Color } from "../../api/MatchApi";
import { Square } from "../../chess/chess-view";
import { Chessboard } from "../../chess/Chessboard";
import { GameState } from "../../services/ChessService";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { TitleCasePipe } from "@angular/common";
import { AvatarComponent } from "../avatar/avatar.component";

export interface Player {
    id: string;
    displayName: string;
    avatar: string;
    color: Color;
}

export interface PieceDragEvent {
    mouseEvent: MouseEvent,
    i: number,
    j: number
}

@Component({
    selector: "chess-board",
    templateUrl: "chess-board.component.html",
    styleUrl: "chess-board.component.css",
    imports: [TimeFormatPipe, TitleCasePipe, AvatarComponent]
})
export class ChessboardComponent implements AfterViewInit {

    readonly SQUARE_WIDTH: number = Square.WIDTH;

    readonly SQUARE_HEIGHT: number = Square.HEIGHT;

    private pieceImgElementRefs = viewChildren("pieceImg", { read: ElementRef });

    private moveListElementRef = viewChild("moveList", { read: ElementRef });

    private boardElementRef = viewChild("board", { read: ElementRef });

    players = input<Player[]>();

    myColor = input<Color>();

    activeColor = input<Color>();

    squares = input.required<Square[][]>();

    timer = input<number>(0);

    moves = input<string[][]>();

    gameState = input<GameState>();

    replay = input<boolean>(false);

    drag = output<PieceDragEvent>();

    resign = output<void>();

    constructor() {
        effect(() => {
            const moves = this.moves();
            if (moves && moves.length > 0) {
                const nativeElement = this.moveListElementRef()?.nativeElement;
                nativeElement.scrollTop = nativeElement.scrollHeight - nativeElement.clientHeight;
            }
        });
    }

    ngAfterViewInit(): void {
        for (let i = 0; i < this.pieceImgElementRefs().length; i++) {
            this.squares()[Math.floor(i / Chessboard.SIZE)][i % Chessboard.SIZE].setPieceImg(this.pieceImgElementRefs()[i]);
        }
    }

    getBoundingClientRect(): DOMRect {
        return this.boardElementRef()?.nativeElement.getBoundingClientRect();
    }

    isEnabled(square: Square): boolean {
        return this.activeColor() !== undefined && this.activeColor() === this.myColor() && square.getColor() === this.myColor() && this.gameState() == "ACTIVE";
    }

    gameOver(): boolean {
        return this.gameState() !== "ACTIVE" && this.gameState() !== "AWAITING_PROMOTION";
    }


}