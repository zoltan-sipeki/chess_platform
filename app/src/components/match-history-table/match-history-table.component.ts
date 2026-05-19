import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { TimeAgoPipe } from "../../pipes/TimeAgoPipe";
import { TimeFormatPipe } from "../../pipes/TimeFormatPipe";
import { MatchHistory } from "../../services/MatchService";

@Component({
    selector: 'match-history-table',
    templateUrl: 'match-history-table.component.html',
    imports: [TimeAgoPipe, TimeFormatPipe, RouterLink]
})
export class MatchHistoryTable {

    matches = input<MatchHistory[]>([]);

    getScoreClass(score: string): string {
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

    getMmrChangeClass(mmrChange: number): string {
        return mmrChange > 0 ? "text-success" : "text-danger";
    }

    getColorClass(color: string): string {
        if (color === "WHITE") {
            return "bg-white";
        }

        if (color === "BLACK") {
            return "bg-black";
        }

        return "";
    }
}