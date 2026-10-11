import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { of, switchMap } from 'rxjs';

import { RecipeFormComponent, RecipeFormResult } from '../../components/recipe-form/recipe-form';
import { RecipeService } from '../../services/recipe.service';

@Component({
  selector: 'app-recipe-create-page',
  imports: [RecipeFormComponent],
  templateUrl: './recipe-create-page.html',
  styleUrl: './recipe-create-page.css',
})
export class RecipeCreatePage {
  message = '';
  errorMessage = '';

  constructor(
    private recipeService: RecipeService,
    private router: Router
  ) {}

  createRecipe(result: RecipeFormResult): void {
    this.message = '';
    this.errorMessage = '';

    const { request, file } = result;

    this.recipeService
      .createRecipe(request)
      .pipe(
        switchMap((recipe) =>
          file
            ? this.recipeService.uploadRecipeImage(recipe.id, file)
            : of(recipe)
        )
      )
      .subscribe({
        next: (recipe) => {
          this.router.navigate(['/recipes', recipe.id]);
        },
        error: (error) => {
          console.error('Could not create recipe:', error);
          this.errorMessage = 'Could not create the recipe.';
        },
      });
  }
}
