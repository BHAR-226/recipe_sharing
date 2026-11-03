import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { RecipeService } from '../../services/recipe.service';
import { RecipeFormComponent } from '../../components/recipe-form/recipe-form';
import { Recipe } from '../../../../shared/models/recipe';
import { RecipeRequest } from '../../../../shared/models/recipe-request';

@Component({
  selector: 'app-recipe-edit-page',
  imports: [RouterLink, RecipeFormComponent],
  templateUrl: './recipe-edit-page.html',
  styleUrl: './recipe-edit-page.css',
})
export class RecipeEditPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private recipeService = inject(RecipeService);
  private cdr = inject(ChangeDetectorRef);

  recipeId!: number;
  recipe?: Recipe;
  isLoading = true;
  isSubmitting = false;
  message = '';
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.errorMessage = 'Identifiant de recette manquant.';
      this.isLoading = false;
      this.cdr.markForCheck();
      return;
    }

    this.recipeId = id;
    this.loadRecipe();
  }

  loadRecipe(): void {
    this.isLoading = true;
    this.cdr.markForCheck();

    this.recipeService.getRecipeById(this.recipeId).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading recipe for editing:', err);
        this.errorMessage = 'Impossible de charger la recette pour modification.';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  updateRecipe(request: RecipeRequest): void {
    this.message = '';
    this.errorMessage = '';
    this.isSubmitting = true;
    this.cdr.markForCheck();

    this.recipeService
      .updateRecipe(this.recipeId, {
        title: request.title,
        description: request.description,
        ingredients: request.ingredients,
        steps: request.steps,
        images: request.images,
      })
      .subscribe({
        next: () => {
          this.isSubmitting = false;
          this.message = 'Recette mise à jour avec succès !';
          this.cdr.markForCheck();
          setTimeout(() => {
            this.router.navigate(['/recipes', this.recipeId]);
          }, 1000);
        },
        error: (err) => {
          console.error('Error updating recipe:', err);
          this.isSubmitting = false;
          this.errorMessage = 'Erreur lors de la mise à jour de la recette.';
          this.cdr.markForCheck();
        },
      });
  }
}
