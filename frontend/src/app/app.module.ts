// import { NgModule } from '@angular/core';
// // NgModule is a decorator that defines a module in Angular
//
// import { BrowserModule } from '@angular/platform-browser';
// // BrowserModule is required to run the app in a browser
//
// import { AppComponent } from './app.component';
// // The root component of the application
//
// import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
// // HttpClientModule allows using HttpClient for making HTTP requests
// // HTTP_INTERCEPTORS is used to provide custom interceptors
//
// import { RouterModule, Routes } from '@angular/router';
// // RouterModule sets up the routing system
// // Routes defines route configurations
//
// import { LoginComponent } from './auth/components/login/login.component';
// import { RegisterComponent } from './auth/components/register/register.component';
// import { FileUploadComponent } from './file-upload/components/file-upload/file-upload.component';
// import { AuthGuard } from './auth/services/auth.guard';
// // Components and guard used in routing
//
// import { JwtInterceptor } from './app/interceptors/jwt.interceptor';
// // Custom HTTP interceptor for attaching JWT tokens to requests
//
// // Define your application routes
// const routes: Routes = [
//   { path: '', redirectTo: 'login', pathMatch: 'full' },
//   { path: 'login', component: LoginComponent },
//   { path: 'register', component: RegisterComponent },
//   { path: 'upload', component: FileUploadComponent, canActivate: [AuthGuard] }
// ];
//
// @NgModule({
//   declarations: [
//     AppComponent,
//     LoginComponent,
//     RegisterComponent,
//     FileUploadComponent
//     // Declare all components used in templates here
//   ],
//   imports: [
//     BrowserModule,
//     HttpClientModule,
//     RouterModule.forRoot(routes)
//     // Import all required Angular modules
//   ],
//   providers: [
//     AuthGuard,
//     {
//       provide: HTTP_INTERCEPTORS,
//       useClass: JwtInterceptor,
//       multi: true
//       // Register JwtInterceptor to handle HTTP requests
//     }
//   ],
//   bootstrap: [AppComponent]
//   // The root component to bootstrap at app startup
// })
// export class AppModule { }
