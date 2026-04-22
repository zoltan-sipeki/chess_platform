import { Routes } from '@angular/router';
import { AuthRedirect } from '../components/auth-redirect/auth-redirect.component';
import { Dashboard } from '../components/dashboard/dashboard.component';
import { Homepage } from '../components/homepage/homepage.component';
import { authGuard } from '../guards/auth-guard';

export const routes: Routes = [
    {
        path: "",
        component: AuthRedirect
    },
    {
        path: "home",
        component: Homepage
    },
    {
        path: "dashboard",
        canActivate: [authGuard],
        component: Dashboard
    }];
