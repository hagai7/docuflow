import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'
import { AuthFormComponent, AuthFormValue } from '../auth-form/auth-form.component';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,        // ← required for routerLink to work
    AuthFormComponent
  ],
  template: `
    <h2 class="page-title">Login</h2>
    <app-auth-form
        [submitLabel]="'Login'"
        (submitted)="onLogin($event)">
    </app-auth-form>
    <p class="switch-msg">
      Don't have an account?
      <a routerLink="/register">Register here</a>
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
export class LoginComponent {
  error = '';

  constructor(
      private auth: AuthService,
      private router: Router
  ) {}

  onLogin({ username, password }: AuthFormValue) {
    this.error = '';
    this.auth.login(username, password).subscribe({
      next: res => {
        if (res.token) {
          this.router.navigate(['/upload']);
        } else {
          this.error = res.message || 'Login failed';
        }
      },
      error: err => {
        this.error = err.error?.message || 'Server error';
      }
    });
  }
}
