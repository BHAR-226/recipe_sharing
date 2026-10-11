import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Recipe } from '../../../../shared/models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { RecipeCommentsComponent } from '../../components/recipe-comments/recipe-comments';
import { RecipeActionsComponent } from '../../components/recipe-actions/recipe-actions';
import { ImageUrlPipe } from '../../../../shared/pipes/image-url.pipe';
import { AuthService } from '../../../auth/services/auth.service';


@Component({
  selector: 'app-recipe-detail-page',
  imports: [
    RecipeCommentsComponent,
    RecipeActionsComponent,
    ImageUrlPipe
  ],
  templateUrl: './recipe-detail-page.html',
  styleUrl: './recipe-detail-page.css',
})
export class RecipeDetailPage implements OnInit{
  recipe?: Recipe;
  protected readonly auth = inject(AuthService);

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.recipeService.getRecipeById(id).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
      },
      error: (error) => {
        console.error('Error loading recipe:', error);
      }
    });
  }
}
