import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiBaseUrl } from '../../../core/config/api.config';

@Injectable({
  providedIn: 'root',
})
export class LikeService {
  private http = inject(HttpClient);

  private get apiUrl(): string {
    return `${getApiBaseUrl()}/like`;
  }

  getLikesCount(recipeId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/recipe/${recipeId}`);
  }

  hasUserLiked(userId: number, recipeId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/status/${userId}/${recipeId}`);
  }

  likeRecipe(recipeId: number, userId: number = 0): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/like`, { userId, recipeId });
  }

  unlikeRecipe(recipeId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/unlike/${userId}/${recipeId}`);
  }
}
