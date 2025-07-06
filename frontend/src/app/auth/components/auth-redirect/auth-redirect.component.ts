// src/app/auth/components/auth-redirect/auth-redirect.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-redirect',
  standalone: true,
  template: `<p>Redirecting...</p>`,
})
export class AuthRedirectComponent implements OnInit {
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/upload']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
