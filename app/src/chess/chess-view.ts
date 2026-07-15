import { ElementRef, Signal, signal, WritableSignal } from "@angular/core";
import { Chessboard } from "./Chessboard";
import { CastlingMove } from "./moves/CastlingMove";
import { EnPassantMove } from "./moves/EnPassantMove";
import { Move, Position } from './moves/Move';
import { Color } from "./pieces/AbstractPiece";
import { PieceType } from "./pieces/Piece";

interface PieceStyle {
    position?: string,
    "z-index"?: number,
    left?: string,
    top?: string,
    transition?: string,
    transform?: string
}

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

    private _pieceStyle: WritableSignal<PieceStyle | null> = signal(null);

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

        this._highlightColor.set(`rgba(${constrain(finalColor[0], 255)}, ${constrain(finalColor[1], 255)}, ${constrain(finalColor[2], 255)}, 0.5)`);
    }
}

export class SquareAnimation {

    private move: Move;

    constructor(move: Move) {
        this.move = move;
    }

    execute(squares: Square[][], board: Chessboard, boardPosition: DOMRect) {
        const from = this.move.getFrom();
        const to = this.move.getTo();

        const { left, top } = boardPosition;

        const source = {
            x: left + from.col * Square.WIDTH,
            y: top + from.row * Square.HEIGHT
        }

        const target = {
            x: left + to.col * Square.WIDTH,
            y: top + to.row * Square.HEIGHT
        };

        squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
            swapSquares(from, to, squares, board);
            highlightMove(this.move, squares, board);
        });

        if (this.move instanceof CastlingMove) {
            const from = this.move.getRookPosition();
            const to = this.move.getRookTargetPosition();

            const { left, top } = boardPosition;

            const source = {
                x: left + from.col * Square.WIDTH,
                y: top + from.row * Square.HEIGHT
            }

            const target = {
                x: left + to.col * Square.WIDTH,
                y: top + to.row * Square.HEIGHT
            };

            squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
                swapSquares(from, to, squares, board);
                highlightMove(this.move, squares, board);
            });
        }
    }

    undo(squares: Square[][], board: Chessboard, boardPosition: DOMRect) {
        const from = this.move.getTo();
        const to = this.move.getFrom();

        const { left, top } = boardPosition;

        const source = {
            x: left + from.col * Square.WIDTH,
            y: top + from.row * Square.HEIGHT
        }

        const target = {
            x: left + to.col * Square.WIDTH,
            y: top + to.row * Square.HEIGHT
        };

        squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
            swapSquares(from, to, squares, board);
            unhighlightMove(this.move, squares, board);
        });

        if (this.move instanceof CastlingMove) {
            const from = this.move.getRookTargetPosition();
            const to = this.move.getRookPosition();

            const { left, top } = boardPosition;

            const source = {
                x: left + from.col * Square.WIDTH,
                y: top + from.row * Square.HEIGHT
            }

            const target = {
                x: left + to.col * Square.WIDTH,
                y: top + to.row * Square.HEIGHT
            };

            squares[from.row][from.col].translate(target.x - source.x, target.y - source.y, () => {
                swapSquares(from, to, squares, board);
                unhighlightMove(this.move, squares, board);
            });
        }
    }

    highlight(squares: Square[][], board: Chessboard) {
        highlightMove(this.move, squares, board);
    }

    unhighlight(squares: Square[][], board: Chessboard) {
        unhighlightMove(this.move, squares, board);
    }
}

export function showMove(move: Move, squares: Square[][], board: Chessboard): void {
    swapSquares(move.getFrom(), move.getTo(), squares, board);

    if (move instanceof EnPassantMove) {
        const cp = move.getCapturedPawnPosition();
        squares[cp.row][cp.col].clearPiece();
    } else if (move instanceof CastlingMove) {
        swapSquares(move.getRookPosition(), move.getRookTargetPosition(), squares, board);
    }
}

function swapSquares(a: Position, b: Position, squares: Square[][], board: Chessboard): void {
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

export function highlightMove(move: Move, squares: Square[][], board: Chessboard): void {
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

    highlightCheck(move, squares, board);
}

export function highlightCheck(move: Move, squares: Square[][], board: Chessboard): void {
    if (move.getCheckStatus() != null) {
        const { row, col } = board.findKing(move.getColor() === "BLACK" ? "WHITE" : "BLACK");
        squares[row][col].highlightCheck();
    }
}

export function unhighlightCheck(move: Move, squares: Square[][], board: Chessboard): void {
    if (move.getCheckStatus() != null) {
        const { row, col } = board.findKing(move.getColor() === "BLACK" ? "WHITE" : "BLACK");
        squares[row][col].unhighlightCheck();
    }
}

export function unhighlightMove(move: Move, squares: Square[][], board: Chessboard): void {
    const a = move.getFrom();
    const b = move.getTo();

    squares[a.row][a.col].unhighlightMove();
    squares[b.row][b.col].unhighlightMove();

    if (move instanceof CastlingMove) {
        const a = (move as CastlingMove).getRookPosition();
        const b = (move as CastlingMove).getRookTargetPosition();
        squares[a.row][a.col].unhighlightMove();
        squares[b.row][b.col].unhighlightMove();
    }

    unhighlightCheck(move, squares, board);
}

export function clearHighlights(squares: Square[][]) {
    for (const row of squares) {
        for (const square of row) {
            square.clearHighlight();
        }
    }
}

export function highlightLegalMoves(moves: Position[], squares: Square[][]): void {
    for (const move of moves) {
        const { row, col } = move;
        squares[row][col].highlightLegalMove();
    }
}

export function unhighlightLegalMoves(squares: Square[][]): void {
    for (const row of squares) {
        for (const square of row) {
            square.unhighlightLegalMove();
        }
    }
}

function constrain(value: number, max: number): number {
    if (value > max) {
        return max;
    }

    return value;
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