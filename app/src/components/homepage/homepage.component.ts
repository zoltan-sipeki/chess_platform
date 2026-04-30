import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../services/AuthService";
import { Navbar } from "../navbar/navbar.component";


@Component({
    selector: 'homepage',
    templateUrl: './homepage.component.html',
    styleUrl: './homepage.component.css',
    imports: [RouterLink, Navbar]
})
export class Homepage implements OnInit {

    private authService: AuthService = inject(AuthService);

    loginUrl = signal<string>("");

    registerUrl = signal<string>("");

    async ngOnInit(): Promise<void> {
        this.loginUrl.set(await this.authService.createLoginUrl());
        this.registerUrl.set(await this.authService.createRegisterUrl());
    }
}