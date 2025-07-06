// src/app/app.component.ts

import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { NgIf, NgForOf } from '@angular/common';
import { AuthService } from './auth/services/auth.service';

interface NavLink {
  label: string;
  link?: string;
  click?: () => void;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, NgIf, NgForOf],
  template: `
    <nav>
      <ng-container *ngFor="let nav of navLinks; let last = last">
        <a *ngIf="nav.link"
           [routerLink]="nav.link">
          {{ nav.label }}
        </a>
        <a *ngIf="nav.click"
           (click)="nav.click()"
           style="cursor: pointer;">
          {{ nav.label }}
        </a>
        <span *ngIf="!last"> | </span>
      </ng-container>
    </nav>
    <router-outlet></router-outlet>
  `,
  styles: [`
    nav a {
      color: inherit;
      text-decoration: none;
      font-style: normal;
      margin: 0 0.5rem;
      cursor: pointer;
    }
    nav a:hover {
      text-decoration: underline;
      color: blue;
    }
  `]
})
export class AppComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get navLinks(): NavLink[] {
    if (this.isLoggedIn) {
      return [{ label: 'Logout', click: () => this.logout() }];
    } else {
      return []; // 🔥 No Login/Register in top nav
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
