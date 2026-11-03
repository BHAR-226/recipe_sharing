import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Recipe } from '../../../shared/models/recipe';
import { RecipeRequest, RecipeUpdateRequest } from '../../../shared/models/recipe-request';
import { getApiBaseUrl } from '../../../core/config/api.config';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private get apiUrl(): string {
    return `${getApiBaseUrl()}/recipes`;
  }

  constructor(private http: HttpClient) {}

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }

  getRecipeById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${this.apiUrl}/${id}`);
  }

  createRecipe(request: RecipeRequest): Observable<Recipe> {
    return this.http.post<Recipe>(`${this.apiUrl}/create-recipe`, request);
  }

  updateRecipe(id: number, request: RecipeUpdateRequest): Observable<Recipe> {
    return this.http.put<Recipe>(`${this.apiUrl}/edit-recipe/${id}`, request);
  }

  deleteRecipe(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchRecipes(title: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/search/${encodeURIComponent(title)}`);
  }

  getUserRecipes(userId: number): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/user/${userId}`);
  }

  getLikeCount(recipeId: number): Observable<number> {
    return this.http.get<number>(`${getApiBaseUrl()}/like/recipe/${recipeId}`);
  }

  likeRecipe(req: { userId: number; recipeId: number }): Observable<void> {
    return this.http.post<void>(`${getApiBaseUrl()}/like/like`, req);
  }

  unlikeRecipe(userId: number, recipeId: number): Observable<void> {
    return this.http.delete<void>(`${getApiBaseUrl()}/like/unlike/${userId}/${recipeId}`);
  }

  getComments(recipeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${getApiBaseUrl()}/comment/recipe/${recipeId}`);
  }

  addComment(req: { content: string; recipeId: number }): Observable<any> {
    return this.http.post<any>(`${getApiBaseUrl()}/comment/new-comment`, req);
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${getApiBaseUrl()}/comment/${id}`);
  }
}
