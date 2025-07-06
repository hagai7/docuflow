// src/main.ts

import { bootstrapApplication } from '@angular/platform-browser';
// bootstrapApplication (Angular 16+) starts a standalone Angular app
// by loading the root component

import { AppComponent } from './app/app.component';
// The root standalone component to bootstrap

import { appConfig } from './app/app.config';
// Application-wide configuration (providers like routes and HttpClient)

bootstrapApplication(AppComponent, appConfig)
  // bootstrapApplication returns a Promise that resolves when the app boots successfully
  // .catch handles any errors that may occur during bootstrap (e.g., misconfiguration)
  .catch(err => {
    console.error('Bootstrap failed:', err);
  });
