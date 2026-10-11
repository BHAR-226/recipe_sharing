import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { of, switchMap } from 'rxjs';

import { Recipe } from '../../../../shared/models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { RecipeFormComponent, RecipeFormResult } from '../../components/recipe-form/recipe-form';

@Component({
  selector: 'app-recipe-edit-page',
  imports: [RecipeFormComponent],
  templateUrl: './recipe-edit-page.html',
  styleUrl: './recipe-edit-page.css',
})
export class RecipeEditPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private recipeService = inject(RecipeService);

  recipe: Recipe | null = null;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage = 'Invalid recipe ID.';
      return;
    }

    this.recipeService.getRecipeById(id).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Could not load the recipe.';
      },
    });
  }

  updateRecipe(result: RecipeFormResult): void {
    if (!this.recipe) return;

    const { request, file } = result;
    const id = this.recipe.id;

    this.recipeService
      .updateRecipe(id, request)
      .pipe(
        switchMap((recipe) =>
          file ? this.recipeService.uploadRecipeImage(id, file) : of(recipe)
        )
      )
      .subscribe({
        next: () => {
          this.router.navigate(['/recipes', id]);
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Could not update the recipe.';
        },
      });
  }
}
