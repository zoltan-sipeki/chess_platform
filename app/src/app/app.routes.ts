import { Routes } from '@angular/router';
import { Login } from '../components/login/login.component';
import { Dashboard } from '../components/dashboard/dashboard.component';
import { Homepage } from '../components/homepage/homepage.component';
import { UserSearchPage } from '../components/user-search-page/user-search-page.component';

export const routes: Routes = [
    {
        path: "login",
        component: Login
    },
    {
        path: "",
        component: Homepage
    },
    {
        path: "dashboard",
        component: Dashboard,
        children: [{
            path: "users",
            component: UserSearchPage
        }]
    }];
