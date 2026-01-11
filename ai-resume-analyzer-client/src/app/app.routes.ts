import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { MainComponent } from './main/main';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'main', component: MainComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
