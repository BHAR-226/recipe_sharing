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
}
