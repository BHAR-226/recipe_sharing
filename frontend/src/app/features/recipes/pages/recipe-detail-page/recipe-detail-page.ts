import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Recipe } from '../../../../shared/models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { LikeService } from '../../services/like.service';
import { CommentService } from '../../services/comment.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ImageService } from '../../../../core/services/image.service';
import { CommentResponse } from '../../../../shared/models/comment';

@Component({
  selector: 'app-recipe-detail-page',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './recipe-detail-page.html',
  styleUrl: './recipe-detail-page.css',
})
export class RecipeDetailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private recipeService = inject(RecipeService);
  private likeService = inject(LikeService);
  private commentService = inject(CommentService);
  private cdr = inject(ChangeDetectorRef);
  authService = inject(AuthService);
  imageService = inject(ImageService);

  recipe?: Recipe;
  isLoading = true;
  errorMessage = '';
  isDeleting = false;
  showDeleteConfirm = false;

  activeImageIndex = 0;

  // Likes
  likesCount = 0;
  isLiked = false;
  isLiking = false;

  // Comments
  comments: CommentResponse[] = [];
  newCommentText = '';
  isSubmittingComment = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.errorMessage = 'Identifiant de recette invalide.';
      this.isLoading = false;
      return;
    }

    this.loadRecipe(id);
  }

  loadRecipe(id: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.recipeService.getRecipeById(id).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
        this.isLoading = false;
        this.cdr.markForCheck();
        this.loadLikes(id);
        this.loadComments(id);
      },
      error: (error) => {
        console.error('Error loading recipe:', error);
        this.errorMessage = 'Impossible de charger les détails de cette recette.';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadLikes(recipeId: number): void {
    this.likeService.getLikesCount(recipeId).subscribe({
      next: (count) => {
        this.likesCount = count || 0;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    const user = this.authService.currentUser();
    if (user) {
      this.likeService.hasUserLiked(user.id, recipeId).subscribe({
        next: (liked) => {
          this.isLiked = liked;
          this.cdr.markForCheck();
        },
        error: () => {},
      });
    }
  }

  toggleLike(): void {
    const user = this.authService.currentUser();
    if (!user) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: `/recipes/${this.recipe?.id}` },
      });
      return;
    }

    if (!this.recipe || this.isLiking) return;
    this.isLiking = true;

    if (this.isLiked) {
      this.likeService.unlikeRecipe(this.recipe.id, user.id).subscribe({
        next: () => {
          this.isLiked = false;
          this.likesCount = Math.max(0, this.likesCount - 1);
          this.isLiking = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.isLiking = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.likeService.likeRecipe(this.recipe.id, user.id).subscribe({
        next: () => {
          this.isLiked = true;
          this.likesCount++;
          this.isLiking = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.isLiking = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  loadComments(recipeId: number): void {
    this.commentService.getRecipeComments(recipeId).subscribe({
      next: (list) => {
        this.comments = list || [];
        this.cdr.markForCheck();
      },
      error: (e) => console.warn('Could not load comments', e),
    });
  }

  addComment(): void {
    if (!this.newCommentText.trim() || !this.recipe) return;

    const user = this.authService.currentUser();
    if (!user) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: `/recipes/${this.recipe.id}` },
      });
      return;
    }

    this.isSubmittingComment = true;
    this.cdr.markForCheck();
    this.commentService
      .createComment({
        content: this.newCommentText.trim(),
        recipeId: this.recipe.id,
        userId: user.id,
      })
      .subscribe({
        next: (created) => {
          this.comments.unshift(created);
          this.newCommentText = '';
          this.isSubmittingComment = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error posting comment:', err);
          this.isSubmittingComment = false;
          this.cdr.markForCheck();
        },
      });
  }

  deleteComment(commentId: number): void {
    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter((c) => c.id !== commentId);
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error deleting comment:', err),
    });
  }

  get canEditOrDelete(): boolean {
    const user = this.authService.currentUser();
    if (!user || !this.recipe) return false;
    return user.id === this.recipe.author?.id || user.role === 'ADMIN';
  }

  get authorInitial(): string {
    return this.recipe?.author?.username
      ? this.recipe.author.username.charAt(0).toUpperCase()
      : 'B';
  }

  get authorAvatarUrl(): string {
    return this.recipe?.author?.avatarUrl
      ? this.imageService.getImageUrl(this.recipe.author.avatarUrl)
      : '';
  }

  getCommentAvatarUrl(avatarUrl?: string): string {
    return avatarUrl ? this.imageService.getImageUrl(avatarUrl) : '';
  }

  setActiveImage(index: number): void {
    if (this.recipe?.images && index >= 0 && index < this.recipe.images.length) {
      this.activeImageIndex = index;
    }
  }

  prevImage(): void {
    if (this.activeImageIndex > 0) {
      this.activeImageIndex--;
    }
  }

  nextImage(): void {
    if (this.recipe?.images && this.activeImageIndex < this.recipe.images.length - 1) {
      this.activeImageIndex++;
    }
  }

  get parsedIngredients(): string[] {
    if (!this.recipe?.ingredients) return [];
    return this.recipe.ingredients
      .split('\n')
      .map((item) => item.trim())
      .filter((item) => item.length > 0);
  }

  get parsedSteps(): string[] {
    if (!this.recipe?.steps) return [];
    return this.recipe.steps
      .split('\n')
      .map((step) => step.trim())
      .filter((step) => step.length > 0);
  }

  toggleDeleteConfirm(): void {
    this.showDeleteConfirm = !this.showDeleteConfirm;
  }

  confirmDelete(): void {
    if (!this.recipe) return;
    this.isDeleting = true;

    this.recipeService.deleteRecipe(this.recipe.id).subscribe({
      next: () => {
        this.isDeleting = false;
        this.router.navigate(['/recipes']);
      },
      error: (err) => {
        console.error('Error deleting recipe:', err);
        this.isDeleting = false;
      },
    });
  }
}
