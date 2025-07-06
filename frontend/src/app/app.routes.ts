// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './auth/components/login/login.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { FileUploadComponent } from './file-upload/components/file-upload/file-upload.component';
import { AuthGuard } from './auth/services/auth.guard';
import { AuthRedirectComponent } from './auth/components/auth-redirect/auth-redirect.component';  // new import

export const routes: Routes = [
  { path: '', component: AuthRedirectComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'upload', component: FileUploadComponent, canActivate: [AuthGuard]},
];
