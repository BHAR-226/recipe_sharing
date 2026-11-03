import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest } from '../../shared/models/auth';
import { UserRequest, UserResponse } from '../../shared/models/user';
import { UserService } from '../../features/users/services/user.service';
import { getApiBaseUrl } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private userService = inject(UserService);

  private get apiUrl(): string {
    return `${getApiBaseUrl()}/auth`;
  }

  readonly token = signal<string | null>(null);
  readonly currentUser = signal<UserResponse | null>(null);
  readonly isLoggedIn = computed(() => !!this.token() && !!this.currentUser());

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      try {
        const storedToken = localStorage.getItem('recipenet_auth_token') || localStorage.getItem('bhar_auth_token');
        const storedUser = localStorage.getItem('recipenet_auth_user') || localStorage.getItem('bhar_auth_user');
        if (storedToken && storedUser) {
          const parsedUser = JSON.parse(storedUser);
          this.token.set(storedToken);
          this.currentUser.set(parsedUser);
          this.userService.setActiveUser(parsedUser);
        }
      } catch (e) {
        console.warn('Could not restore auth session from localStorage', e);
      }
    }
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  register(request: UserRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  logout(): void {
    this.token.set(null);
    this.currentUser.set(null);
    this.userService.setActiveUser(null);

    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('recipenet_auth_token');
      localStorage.removeItem('recipenet_auth_user');
      localStorage.removeItem('recipenet_active_user');
      localStorage.removeItem('bhar_auth_token');
      localStorage.removeItem('bhar_auth_user');
      localStorage.removeItem('bhar_active_user');
    }

    this.router.navigate(['/recipes']);
  }

  getToken(): string | null {
    return this.token();
  }

  updateCurrentUser(user: UserResponse): void {
    this.currentUser.set(user);
    this.userService.setActiveUser(user);
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('recipenet_auth_user', JSON.stringify(user));
    }
  }

  private handleAuthSuccess(res: AuthResponse): void {
    this.token.set(res.token);
    this.currentUser.set(res.user);
    this.userService.setActiveUser(res.user);

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('recipenet_auth_token', res.token);
      localStorage.setItem('recipenet_auth_user', JSON.stringify(res.user));
    }
  }
}
