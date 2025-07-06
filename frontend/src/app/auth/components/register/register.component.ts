
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'
import { AuthFormComponent, AuthFormValue } from '../auth-form/auth-form.component';
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,        // ← required for routerLink to work
    AuthFormComponent
  ],
  template: `
    <h2 class="page-title">Register</h2>
    <app-auth-form
        [submitLabel]="'Register'"
        (submitted)="onRegister($event)">
        [usernameError]="usernameExists ? 'Username already exists' : ''">
    </app-auth-form>
    <p class="switch-msg">
      Already have an account?
      <a routerLink="/login">Login here</a>
    </p>
    <p class="error" *ngIf="error">{{ error }}</p>
  `,
  styles: [`
    .page-title { text-align: center; margin-top: 2rem; }
    .switch-msg {
      text-align: center;
      margin: 1rem;
    }
    /* pointer cursor on link */
    .switch-msg a {
      cursor: pointer;
      color: inherit;
      text-decoration: underline;
    }
    .error {
      color: red;
      text-align: center;
    }
  `]
})
export class RegisterComponent {
  error = '';

  constructor(
      private auth: AuthService,
      private router: Router
  ) {}

  onRegister({ username, password }: AuthFormValue) {
    this.error = '';
    this.auth.register(username, password).subscribe({
      next: () => {
        this.auth.login(username, password).subscribe({
          next: res => {
            if (res.token) {
              this.router.navigate(['/upload']);
            } else {
              this.error = 'Registration succeeded but login failed.';
            }
          },
          error: err => {
            this.error = err.error?.message || 'Login error after registration';
          }
        });
      },
      error: err => {
        this.error = err.error?.message || 'Registration failed';
      }
    });
  }
}
