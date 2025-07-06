// src/app/guards/auth.guard.ts

import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';  // ← import AuthService

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,  // ← inject AuthService
    private router: Router
  ) {}

  canActivate(): boolean {
    // Use AuthService.isLoggedIn() so you’re not tied to localStorage directly,
    // and so you can later add expiration checks, etc.
    if (this.authService.isLoggedIn()) {
      return true;
    }

    // If there’s no valid token, redirect to /login
    this.router.navigate(['/login']);
    return false;
  }
}
