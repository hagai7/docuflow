// src/app/services/auth.service.ts

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token?: string;   // returned on successful login
  message?: string; // optional status or error messages
}

@Injectable({
  providedIn: 'root'
})
// Marks this class as "injectable," allowing Angular’s dependency injection to create and provide its instance.
// 'providedIn: root' makes the service a singleton — one shared instance for the entire app.
// Without this, Angular wouldn't know how or where to create the service instance,
// and you might have to manually provide it in module providers or risk multiple instances being created.

export class AuthService {
  private baseUrl = 'http://localhost:8080/auth';
  private readonly tokenKey = 'jwt_token';

  constructor(private http: HttpClient) {}

  /**
   * Registers a new user. The back end typically returns a message
   * (no token) on successful registration.
   */
  register(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.baseUrl}/register`,
      { username, password }
    );
  }

  /**
   * Logs in an existing user. On success, stores the received JWT
   * in localStorage under the key 'jwt_token'.
   */
  login(username: string, password: string): Observable<AuthResponse> {
    // Observable (RxJS): represents an async data stream you can subscribe to for updates
    // RxJS is a library for handling async streams in Angular, with operators like pipe, tap, map, filter
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, { username, password })
      // post: sends HTTP POST request with username/password, expects AuthResponse type back
      .pipe(
        // pipe (RxJS operator): chains multiple operators to process or react to the Observable's emitted values
        tap((res: AuthResponse) => {
          // tap: side-effect operator to run code (like saving token) without changing the Observable's emitted values
          if (res.token) {
            localStorage.setItem(this.tokenKey, res.token);
            // localStorage: browser storage to save key-value data persistently on the user's device
            // Data remains saved even after page reload or browser close and is accessible only to the same site
          }
        })
      );
  }



  /**
   * Returns the stored JWT (or null if none).
   */
  getJwtToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Clears the stored JWT, effectively “logging out” the user.
   */
  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  /**
   * Decode a JWT token payload
   */
  private decodeToken(token: string): any | null {
    try {
      const payload = token.split('.')[1];
      const decodedJson = atob(payload); // base64 decode
      return JSON.parse(decodedJson);
    } catch {
      return null;
    }
  }

  /**
   * Check if token is expired
   */
  isTokenExpired(token: string | null): boolean {
    if (!token) return true;

    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return true;

    const expiryTime = decoded.exp * 1000; // exp is in seconds, convert to ms
    return Date.now() > expiryTime;
  }

  /**
   * Check if user is logged in AND token is NOT expired
   */
  isLoggedIn(): boolean {
    const token = this.getJwtToken();
    return !!token && !this.isTokenExpired(token);
  }
}
