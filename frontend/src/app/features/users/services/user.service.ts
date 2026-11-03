import { Injectable, inject, signal, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Page, UserProfileRequest, UserRequest, UserResponse, UserSummary } from '../../../shared/models/user';
import { getApiBaseUrl } from '../../../core/config/api.config';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);

  private get userApiUrl(): string {
    return `${getApiBaseUrl()}/users`;
  }

  private get followApiUrl(): string {
    return `${getApiBaseUrl()}/follow`;
  }

  // State: current active user profile in the app
  readonly activeUser = signal<UserResponse | null>(null);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      try {
        const stored = localStorage.getItem('recipenet_active_user') || localStorage.getItem('bhar_active_user');
        if (stored) {
          this.activeUser.set(JSON.parse(stored));
        }
      } catch (e) {
        console.warn('Could not read active user from localStorage', e);
      }
    }
  }

  setActiveUser(user: UserResponse | null): void {
    this.activeUser.set(user);
    if (isPlatformBrowser(this.platformId)) {
      if (user) {
        localStorage.setItem('recipenet_active_user', JSON.stringify(user));
      } else {
        localStorage.removeItem('recipenet_active_user');
        localStorage.removeItem('bhar_active_user');
      }
    }
  }

  getActiveUserId(): number {
    return this.activeUser()?.id ?? 1;
  }

  /* ------------------- User API ------------------- */

  getUsers(page = 0, size = 12): Observable<Page<UserResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<UserResponse>>(this.userApiUrl, { params });
  }

  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.userApiUrl}/${id}`);
  }

  getUserByUsername(username: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.userApiUrl}/username/${encodeURIComponent(username)}`);
  }

  getUserByEmail(email: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.userApiUrl}/email/${encodeURIComponent(email)}`);
  }

  createUser(request: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.userApiUrl, request).pipe(
      tap((created) => {
        if (!this.activeUser()) {
          this.setActiveUser(created);
        }
      })
    );
  }

  updateProfile(id: number, request: UserProfileRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.userApiUrl}/${id}`, request).pipe(
      tap((updated) => {
        if (this.activeUser()?.id === id) {
          this.setActiveUser(updated);
        }
      })
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.userApiUrl}/${id}`).pipe(
      tap(() => {
        if (this.activeUser()?.id === id) {
          this.setActiveUser(null);
        }
      })
    );
  }

  /* ------------------- Follow API ------------------- */

  getFollowers(userId: number): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.followApiUrl}/followers/${userId}`);
  }

  getFollowing(userId: number): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.followApiUrl}/following/${userId}`);
  }

  followUser(followerId: number, followingId: number): Observable<void> {
    return this.http.post<void>(`${this.followApiUrl}/follow`, { followerId, followingId });
  }

  unfollowUser(followerId: number, followingId: number): Observable<void> {
    return this.http.delete<void>(`${this.followApiUrl}/${followerId}/${followingId}`);
  }
}
