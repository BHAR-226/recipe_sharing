import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { RecipeFormComponent } from '../../components/recipe-form/recipe-form';
import { RecipeService } from '../../services/recipe.service';
import { RecipeRequest } from '../../../../shared/models/recipe-request';

@Component({
  selector: 'app-recipe-create-page',
  imports: [RouterLink, RecipeFormComponent],
  templateUrl: './recipe-create-page.html',
  styleUrl: './recipe-create-page.css',
})
export class RecipeCreatePage {
  private recipeService = inject(RecipeService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  message = '';
  errorMessage = '';
  isSubmitting = false;

  createRecipe(request: RecipeRequest): void {
    this.message = '';
    this.errorMessage = '';
    this.isSubmitting = true;
    this.cdr.markForCheck();

    this.recipeService.createRecipe(request).subscribe({
      next: (created) => {
        this.isSubmitting = false;
        this.message = 'Votre recette a été publiée avec succès !';
        this.cdr.markForCheck();
        setTimeout(() => {
          this.router.navigate(['/recipes', created.id]);
        }, 1200);
      },
      error: (error) => {
        console.error('Error creating recipe:', error);
        this.isSubmitting = false;
        this.errorMessage =
          'Une erreur est survenue lors de la création de la recette. Veuillez vérifier les champs.';
        this.cdr.markForCheck();
      },
    });
  }
}
