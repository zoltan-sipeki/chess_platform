import { Component, inject, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../services/AuthService";
import { Navbar } from "../navbar/navbar.component";


@Component({
    selector: 'homepage',
    templateUrl: './homepage.component.html',
    styleUrl: './homepage.component.css',
    standalone: true,
    imports: [RouterLink, Navbar]
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