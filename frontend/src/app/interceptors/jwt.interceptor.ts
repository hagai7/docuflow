import { Injectable } from '@angular/core';
import {
  HttpInterceptor, // Interface to implement for intercepting HTTP requests/responses globally
  HttpRequest,     // Represents an outgoing HTTP request, immutable and can be cloned with modifications
  HttpHandler,     // Handles forwarding the HTTP request to the next interceptor or to the backend
  HttpEvent        // Represents an event from the HTTP request stream (e.g., response, progress)
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const isAuthEndpoint = req.url.endsWith('/login') || req.url.endsWith('/register');
    const token = this.authService.getJwtToken();

    if (!token || isAuthEndpoint) {
      return next.handle(req); // skip attaching token
    }

    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next.handle(authReq);
  }
}
