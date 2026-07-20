import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { TimeAgoPipe } from "../../pipes/TimeAgoPipe";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { MatchHistory } from "../../api/MatchApi";
import { SignedNumberPipe } from "../../pipes/SignedNumberPipe";
import { TitleCasePipe } from "@angular/common";

@Component({
    selector: 'match-history-table',
    templateUrl: 'match-history-table.component.html',
    imports: [TimeAgoPipe, TimeFormatPipe, RouterLink, SignedNumberPipe, TitleCasePipe]
})
export class MatchHistoryTable {

    matches = input<MatchHistory[]>([]);

    getOutcomeClass(score: string): string {
        switch (score) {
            case "WIN":
                return "bg-success-subtle text-success";
            case "DRAW":
                return "bg-secondary-subtle text-secondary";
            case "LOSS":
                return "bg-danger-subtle text-danger";
        }

        return "";
    }

    getMmrChangeClass(mmrChange?: number): string {
        if (mmrChange == null) {
            return "";
        }

        return mmrChange > 0 ? "text-success" : "text-danger";
    }

    getColorIcon(color: string): string {
        if (color === "WHITE") {
            return String.fromCodePoint(9812);
        }

        if (color === "BLACK") {
            return String.fromCodePoint(9818);
        }

        return "";
    }
}