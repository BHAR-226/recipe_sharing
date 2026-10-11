import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Recipe } from '../../../shared/models/recipe';
import { RecipeRequest } from '../../../shared/models/recipe-request';
import { RecipeUpdateRequest } from '../../../shared/models/recipe-update-request';
import { Comment } from '../../../shared/models/comment';
import { CommentRequest } from '../../../shared/models/comment-request';
import { LikeRequest } from '../../../shared/models/like-request';
import { API_BASE } from '../../../shared/api';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {

  private readonly apiUrl = `${API_BASE}/recipes`;

  constructor(private http: HttpClient) {}

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }
  
  getRecipeById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${this.apiUrl}/${id}`);
  }

  createRecipe(request: RecipeRequest): Observable<Recipe> {
    return this.http.post<Recipe>(
      `${this.apiUrl}/create-recipe`,
      request
      );
  }
  updateRecipe(id: number, request: RecipeUpdateRequest): Observable<Recipe> {
  return this.http.put<Recipe>(
    `${this.apiUrl}/edit-recipe/${id}`,
    request
    );
  }

  uploadRecipeImage(id: number, file: File): Observable<Recipe> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<Recipe>(
      `${this.apiUrl}/${id}/image`,
      formData
    );
  }

  deleteRecipeImage(id: number): Observable<Recipe> {
    return this.http.delete<Recipe>(`${this.apiUrl}/${id}/image`);
  }

  getComments(recipeId: number): Observable<Comment[]> {
  return this.http.get<Comment[]>(
    `${API_BASE}/comment/recipe/${recipeId}`
  );
}

addComment(request: CommentRequest): Observable<Comment> {
  return this.http.post<Comment>(
    `${API_BASE}/comment/new-comment`,
    request
  );
}

deleteComment(id: number): Observable<void> {
  return this.http.delete<void>(
    `${API_BASE}/comment/${id}`
  );
}

getLikeCount(recipeId: number): Observable<number> {
  return this.http.get<number>(
    `${API_BASE}/like/recipe/${recipeId}`
  );
}

likeRecipe(request: LikeRequest): Observable<void> {
  return this.http.post<void>(
    `${API_BASE}/like/like`,
    request
  );
}

unlikeRecipe(userId: number, recipeId: number): Observable<void> {
  return this.http.delete<void>(
    `${API_BASE}/like/unlike/${userId}/${recipeId}`
  );
}
}
