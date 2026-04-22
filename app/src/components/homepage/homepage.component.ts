import { Component, inject, signal } from "@angular/core";
import { AuthService } from "../../services/AuthService";


@Component({
    selector: 'homepage',
    templateUrl: './homepage.component.html',
    styleUrl: './homepage.component.css',
    standalone: true
})
export class Homepage {

    private authService: AuthService = inject(AuthService);

    public loginUrl = signal<string>("");

    public registerUrl = signal<string>("");

    public async ngOnInit(): Promise<void> {
        this.loginUrl.set(await this.authService.createLoginUrl());
        this.registerUrl.set(await this.authService.createRegisterUrl());
    }
}