import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, switchMap, tap } from 'rxjs';

import { API_BASE } from '../../../shared/api';
import { User } from '../../../shared/models/user';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE}/auth`;

  private readonly _currentUser = signal<User | null>(null);

  readonly currentUser = this._currentUser.asReadonly();
  readonly userId = computed(() => this._currentUser()?.id ?? 0);
  readonly isAdmin = computed(
    () => this._currentUser()?.role?.toUpperCase() === 'ADMIN'
  );
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  primeCsrf(): Observable<string> {
    return this.http.get(`${this.baseUrl}/csrf`, { responseType: 'text' });
  }

  login(email: string, password: string): Observable<User> {
    return this.primeCsrf().pipe(
      switchMap(() =>
        this.http.post<User>(`${this.baseUrl}/login`, { email, password })
      ),
      tap((user) => this._currentUser.set(user))
    );
  }

  register(username: string, email: string, password: string): Observable<User> {
    return this.primeCsrf().pipe(
      switchMap(() =>
        this.http.post<User>(`${this.baseUrl}/register`, {
          username,
          email,
          password,
        })
      )
    );
  }

  loadCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/me`).pipe(
      tap({
        next: (user) => this._currentUser.set(user),
        error: () => this._currentUser.set(null),
      })
    );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.baseUrl}/logout`, {})
      .pipe(tap(() => this._currentUser.set(null)));
  }
}
