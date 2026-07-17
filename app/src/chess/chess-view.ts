import { ElementRef, Signal, signal, WritableSignal } from "@angular/core";
import { Chessboard } from "./Chessboard";
import { CastlingMove } from "./moves/CastlingMove";
import { EnPassantMove } from "./moves/EnPassantMove";
import { Move, Position } from './moves/Move';
import { Color, PieceType } from "./pieces/Piece";

const pieceImageMap: { [key: string]: string } = {
    "BLACK-PAWN": "imgs/pieces/black-pawn.svg",
    "BLACK-ROOK": "imgs/pieces/black-rook.svg",
    "BLACK-KNIGHT": "imgs/pieces/black-knight.svg",
    "BLACK-BISHOP": "imgs/pieces/black-bishop.svg",
    "BLACK-QUEEN": "imgs/pieces/black-queen.svg",
    "BLACK-KING": "imgs/pieces/black-king.svg",
    "WHITE-PAWN": "imgs/pieces/white-pawn.svg",
    "WHITE-ROOK": "imgs/pieces/white-rook.svg",
    "WHITE-KNIGHT": "imgs/pieces/white-knight.svg",
    "WHITE-BISHOP": "imgs/pieces/white-bishop.svg",
    "WHITE-QUEEN": "imgs/pieces/white-queen.svg",
    "WHITE-KING": "imgs/pieces/white-king.svg",
    "NULL": "imgs/pieces/null-piece.png"
}

const pieceCodePointMap: { [key: string]: string } = {
    "BLACK-PAWN": String.fromCodePoint(9823),
    "BLACK-ROOK": String.fromCodePoint(9820),
    "BLACK-KNIGHT": String.fromCodePoint(9822),
    "BLACK-BISHOP": String.fromCodePoint(9821),
    "BLACK-QUEEN": String.fromCodePoint(9819),
    "BLACK-KING": String.fromCodePoint(9818),
    "WHITE-PAWN": String.fromCodePoint(9817),
    "WHITE-ROOK": String.fromCodePoint(9814),
    "WHITE-KNIGHT": String.fromCodePoint(9816),
    "WHITE-BISHOP": String.fromCodePoint(9815),
    "WHITE-QUEEN": String.fromCodePoint(9813),
    "WHITE-KING": String.fromCodePoint(9812),
}

interface SquarePieceStyle {
    position?: string,
    "z-index"?: number,
    left?: string,
    top?: string,
    transition?: string,
    transform?: string
}

interface HightlightColor {
    color: typeof LEGAL_MOVE_HIGHLIGHT | typeof MOVE_HIGHLIGHT | typeof CHECK_HIGHLIGHT;
    active: boolean
}

interface HighlightColors {
    legalMove: HightlightColor;
    move: HightlightColor;
    check: HightlightColor
}

const LEGAL_MOVE_HIGHLIGHT = [0, 255, 0] as const;

const MOVE_HIGHLIGHT = [255, 255, 0] as const;

const CHECK_HIGHLIGHT = [255, 0, 0] as const;

export class Square {

    static readonly WIDTH = 45;

    static readonly HEIGHT = 45;

    private _pieceStyle: WritableSignal<SquarePieceStyle | null> = signal(null);

    readonly pieceStyle = this._pieceStyle.asReadonly();

    private _highlightColors: HighlightColors = {
        legalMove: { color: LEGAL_MOVE_HIGHLIGHT, active: false },
        move: { color: MOVE_HIGHLIGHT, active: false },
        check: { color: CHECK_HIGHLIGHT, active: false }
    }

    private activeColorCount: number = 0;

    private _highlightColor: WritableSignal<string> = signal("");

    readonly highlightColor = this._highlightColor.asReadonly();

    private _pieceImg: WritableSignal<string>;

    readonly pieceImg: Signal<string>;

    private piece?: PieceType;

    private color?: Color;

    private imgElementRef!: ElementRef<HTMLImageElement>;

    constructor(piece?: PieceType, color?: Color) {
        this.piece = piece;
        this.color = color;
        if (this.piece == null && this.color == null) {
            this._pieceImg = signal(pieceImageMap["NULL"]);
        }
        else {
            this._pieceImg = signal(pieceImageMap[`${color}-${piece}`]);
        }
        this.pieceImg = this._pieceImg.asReadonly();
    }

    setPieceImg(ref: ElementRef<HTMLImageElement>): void {
        this.imgElementRef = ref;
    }

    getPiece(): PieceType | undefined {
        return this.piece;
    }

    getColor(): Color | undefined {
        return this.color;
    }

    setPiece(piece: PieceType, color: Color): void {
        this.piece = piece;
        this.color = color;
        this._pieceImg.set(pieceImageMap[`${color}-${piece}`]);
    }

    clearPiece(): void {
        this.piece = undefined;
        this.color = undefined;
        this._pieceImg.set(pieceImageMap["NULL"]);
    }

    moveTo(x: number, y: number): void {
        this._pieceStyle.update(coords => {
            if (coords == null) {
                return {
                    position: "fixed",
                    "z-index": 1000,
                    left: `${x}px`,
                    top: `${y}px`
                }
            }

            return { ...coords, left: `${x}px`, top: `${y}px` };
        });
    }

    translate(dx: number, dy: number, transitionEndCallback: () => void): void {
        this._pieceStyle.set({
            position: "fixed",
            "z-index": 1000,
            transition: "transform 0.2s",
            transform: `translate(${dx}px, ${dy}px)`,
        });

        this.imgElementRef.nativeElement.addEventListener("transitionend", () => {
            transitionEndCallback();
            this.resetPiecePosition();
        }, { once: true });
    }

    resetPiecePosition(): void {
        this._pieceStyle.set(null);
    }

    unhighlightMove(): void {
        if (!this._highlightColors.move.active) {
            return;
        }

        this._highlightColors.move.active = false;
        this.activeColorCount--;
        this.highlight();
    }

    highlightMove(): void {
        this._highlightColors.move.active = true;
        this.activeColorCount++;
        this.highlight();
    }

    unhighlightCheck(): void {
        if (!this._highlightColors.check.active) {
            return;
        }

        this._highlightColors.check.active = false;
        this.activeColorCount--;
        this.highlight();
    }

    highlightCheck(): void {
        this._highlightColors.check.active = true;
        this.activeColorCount++;
        this.highlight();
    }

    highlightLegalMove(): void {
        this._highlightColors.legalMove.active = true;
        this.activeColorCount++;
        this.highlight();
    }

    unhighlightLegalMove(): void {
        if (!this._highlightColors.legalMove.active) {
            return;
        }

        this._highlightColors.legalMove.active = false;
        this.activeColorCount--;
        this.highlight();
    }

    clearHighlight(): void {
        for (const color of Object.values(this._highlightColors)) {
            color.active = false;
        }

        this.activeColorCount = 0;
        this.highlight();
    }

    private highlight(): void {
        if (this.activeColorCount === 0) {
            this._highlightColor.set("");
            return;
        }

        const finalColor = [0, 0, 0];
        for (const color of Object.values(this._highlightColors)) {
            if (color.active) {
                finalColor[0] = color.color[0] / this.activeColorCount;
                finalColor[1] = color.color[1] / this.activeColorCount;
                finalColor[2] = color.color[2] / this.activeColorCount;
            }
        }

        this._highlightColor.set(`rgba(${Math.min(finalColor[0], 255)}, ${Math.min(finalColor[1], 255)}, ${Math.min(finalColor[2], 255)}, 0.5)`);
    }
}

export class SquareAnimation {

    private static transitionEndCallbacks: (() => void)[] = [];

    static execute(move: Move, squares: Square[][], board: Chessboard, boardPosition: DOMRect) {
        const from = move.getFrom();
        const to = move.getTo();

        const { left, top } = boardPosition;

        const source = {
            x: left + from.col * Square.WIDTH,
            y: top + from.row * Square.HEIGHT
        }

        const target = {
            x: left + to.col * Square.WIDTH,
            y: top + to.row * Square.HEIGHT
        };

        SquareAnimation.transitionEndCallbacks.push(() => {
            Squares.clearHighlights(squares);
            Squares.swap(from, to, squares, board);
            Squares.highlightMove(move, squares, board);
        });

        squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
            SquareAnimation.runTransitionEndCallbacks();
        });

        if (move instanceof CastlingMove) {
            const from = move.getRookPosition();
            const to = move.getRookTargetPosition();

            const { left, top } = boardPosition;

            const source = {
                x: left + from.col * Square.WIDTH,
                y: top + from.row * Square.HEIGHT
            }

            const target = {
                x: left + to.col * Square.WIDTH,
                y: top + to.row * Square.HEIGHT
            };

            SquareAnimation.transitionEndCallbacks.push(() => {
                Squares.swap(from, to, squares, board);
            });

            squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
                SquareAnimation.runTransitionEndCallbacks();
            });
        }
    }

    static undo(move: Move, squares: Square[][], board: Chessboard, boardPosition: DOMRect) {
        const from = move.getTo();
        const to = move.getFrom();

        const { left, top } = boardPosition;

        const source = {
            x: left + from.col * Square.WIDTH,
            y: top + from.row * Square.HEIGHT
        }

        const target = {
            x: left + to.col * Square.WIDTH,
            y: top + to.row * Square.HEIGHT
        };

        SquareAnimation.transitionEndCallbacks.push(() => {
            Squares.swap(from, to, squares, board);
            Squares.unhighlightMove(move, squares, board);
        });

        squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
            SquareAnimation.runTransitionEndCallbacks();
        });

        if (move instanceof CastlingMove) {
            const from = move.getRookTargetPosition();
            const to = move.getRookPosition();

            const { left, top } = boardPosition;

            const source = {
                x: left + from.col * Square.WIDTH,
                y: top + from.row * Square.HEIGHT
            }

            const target = {
                x: left + to.col * Square.WIDTH,
                y: top + to.row * Square.HEIGHT
            };

            SquareAnimation.transitionEndCallbacks.push(() => {
                Squares.swap(from, to, squares, board);
            });

            squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
                SquareAnimation.runTransitionEndCallbacks();
            });
        }
    }

    private static runTransitionEndCallbacks(): void {
        for (const callback of SquareAnimation.transitionEndCallbacks) {
            callback();
        }
        SquareAnimation.transitionEndCallbacks = [];
    }
}

export class Squares {

    static showMove(move: Move, squares: Square[][], board: Chessboard): void {
        Squares.swap(move.getFrom(), move.getTo(), squares, board);

        if (move instanceof EnPassantMove) {
            const cp = move.getCapturedPawnPosition();
            squares[cp.row][cp.col].clearPiece();
        } else if (move instanceof CastlingMove) {
            Squares.swap(move.getRookPosition(), move.getRookTargetPosition(), squares, board);
        }
    }

    static swap(a: Position, b: Position, squares: Square[][], board: Chessboard): void {
        const pa = board.getPiece(a.row, a.col);
        const pb = board.getPiece(b.row, b.col);

        const sa = squares[a.row][a.col];
        const sb = squares[b.row][b.col];

        if (pa == null) {
            sa.clearPiece();
        } else {
            sa.setPiece(pa.getType(), pa.getColor());
        }

        if (pb == null) {
            sb.clearPiece();
        } else {
            sb.setPiece(pb.getType(), pb.getColor());
        }

        sa.resetPiecePosition();
    }

    static highlightMove(move: Move, squares: Square[][], board: Chessboard): void {
        const a = move.getFrom();
        const b = move.getTo();

        squares[a.row][a.col].highlightMove();
        squares[b.row][b.col].highlightMove();

        if (move instanceof CastlingMove) {
            const a = move.getRookPosition();
            const b = move.getRookTargetPosition();
            squares[a.row][a.col].highlightMove();
            squares[b.row][b.col].highlightMove();
        }

        Squares.highlightCheck(move, squares, board);
    }

    static highlightCheck(move: Move, squares: Square[][], board: Chessboard): void {
        if (move.getCheckStatus() != null) {
            const { row, col } = board.findKing(move.getColor() === "BLACK" ? "WHITE" : "BLACK");
            squares[row][col].highlightCheck();
        }
    }

    static unhighlightCheck(move: Move, squares: Square[][], board: Chessboard): void {
        if (move.getCheckStatus() != null) {
            const { row, col } = board.findKing(move.getColor() === "BLACK" ? "WHITE" : "BLACK");
            squares[row][col].unhighlightCheck();
        }
    }

    static unhighlightMove(move: Move, squares: Square[][], board: Chessboard): void {
        const a = move.getFrom();
        const b = move.getTo();

        Squares.unhighlightCheck(move, squares, board);

        squares[a.row][a.col].unhighlightMove();
        squares[b.row][b.col].unhighlightMove();

        if (move instanceof CastlingMove) {
            const a = move.getRookPosition();
            const b = move.getRookTargetPosition();
            squares[a.row][a.col].unhighlightMove();
            squares[b.row][b.col].unhighlightMove();
        }

    }

    static clearHighlights(squares: Square[][]) {
        for (const row of squares) {
            for (const square of row) {
                square.clearHighlight();
            }
        }
    }

    static highlightLegalMoves(moves: Position[], squares: Square[][]): void {
        for (const move of moves) {
            const { row, col } = move;
            squares[row][col].highlightLegalMove();
        }
    }

    static unhighlightLegalMoves(squares: Square[][]): void {
        for (const row of squares) {
            for (const square of row) {
                square.unhighlightLegalMove();
            }
        }
    }

    static create(): Square[][] {
        const squares = [];
        for (let i = 0; i < Chessboard.SIZE; i++) {
            const row = [];
            for (let j = 0; j < Chessboard.SIZE; j++) {
                row.push(new Square());
            }
            squares.push(row);
        }
        return squares;
    }
}


export function toAlgebraicNotation(pos: Position): string {
    return String.fromCharCode("a".charCodeAt(0) + pos.col) + String.fromCharCode("0".charCodeAt(0) + (Chessboard.SIZE - pos.row));
}

export function toCodePoint(piece: string): string {
    return pieceCodePointMap[piece];
}

export function getPieceImg(color: Color, piece: PieceType): string {
    return pieceImageMap[`${color}-${piece}`];
}