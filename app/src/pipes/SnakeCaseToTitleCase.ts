import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "snakeCaseToTitleCase",
})
export class SnakeCaseToTitleCasePipe implements PipeTransform {
    transform(value?: string): string {
        if (value == null) {
            return "";
        }

        let result = "";

        for (let i = 0; i < value.length; i++) {
            if (i === 0 || value[i - 1] === "_") {
                result += value[i].toUpperCase();
            } else if (value[i] === "_") {
                result += " ";
            } else {
                result += value[i].toLowerCase();
            }
        }

        return result;
    }
}