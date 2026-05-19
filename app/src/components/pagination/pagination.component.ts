import { Component, computed, input, model, output, signal } from "@angular/core";
import { NgbPagination } from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: "pagination",
    templateUrl: "pagination.component.html",
    imports: [NgbPagination]
})
export class Pagination {

    pageSize = input<number>(10);

    collectionSize = input.required<number>();

    page = model<number>(1);

    pageChange = output<number>();

    itemsShown = computed(() => Math.min(this.page() * this.pageSize(), this.collectionSize()));

}