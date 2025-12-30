import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';


import { routes } from './app/app.routes';
import { provideRouter } from '@angular/router';
import { jwtInterceptor } from './app/interceptors/jwt.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { Login } from './app/login/login';
import { ShellComponent } from './app/shell.component';

bootstrapApplication(ShellComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor]))
  ]
}).catch(err => console.error(err));