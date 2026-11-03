
import { Component, Input, OnInit } from '@angular/core';
import { RecipeService } from '../../services/recipe.service';

@Component({
  selector: 'app-recipe-actions',
  imports: [],
  templateUrl: './recipe-actions.html',
  styleUrl: './recipe-actions.css',
})
export class RecipeActionsComponent implements OnInit {
  @Input({ required: true }) recipeId!: number;
  @Input({ required: true }) userId!: number;

  likeCount = 0;
  liked = false;
  loading = false;
  errorMessage = '';

  constructor(private recipeService: RecipeService) {}

  ngOnInit(): void {
    this.loadLikeCount();
  }

  loadLikeCount(): void {
    this.recipeService.getLikeCount(this.recipeId).subscribe({
      next: (count) => {
        this.likeCount = count;
      },
      error: () => {
        this.errorMessage = 'Could not load likes.';
      },
    });
  }

  toggleLike(): void {
    if (!this.userId || this.loading) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request = this.liked
      ? this.recipeService.unlikeRecipe(this.userId, this.recipeId)
      : this.recipeService.likeRecipe({
          userId: this.userId,
          recipeId: this.recipeId,
        });

    request.subscribe({
      next: () => {
        this.liked = !this.liked;
        this.likeCount += this.liked ? 1 : -1;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not update your like.';
        this.loading = false;
      },
    });
  }
}