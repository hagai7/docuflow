// src/app/app.config.ts

import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import { JwtInterceptor } from './interceptors/jwt.interceptor';

import { routes } from './app.routes';
// Import the application's route definitions

export const appConfig: ApplicationConfig = {
  providers: [
    // provideRouter sets up routing in the application using the specified routes
    provideRouter(routes),

    // provideHttpClient sets up Angular's HttpClient service in the DI (Dependency Injection) system
    // withInterceptorsFromDi enables HttpClient to use HTTP interceptors
    // registered via Angular's DI (e.g., JwtInterceptor)
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ]
};
