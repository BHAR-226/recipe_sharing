import { Component, Input, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Recipe } from '../../../../shared/models/recipe';
import { LikeService } from '../../services/like.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ImageService } from '../../../../core/services/image.service';

@Component({
  selector: 'app-recipe-card',
  imports: [RouterLink, DatePipe],
  templateUrl: './recipe-card.html',
  styleUrl: './recipe-card.css',
})
export class RecipeCard implements OnInit {
  private likeService = inject(LikeService);
  public imageService = inject(ImageService);
  public authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  @Input({ required: true }) recipe!: Recipe;
  @Input() showAuthor = true;

  likesCount = 0;
  isLiked = false;
  isLiking = false;
  currentImageIndex = 0;

  private touchStartX = 0;

  ngOnInit(): void {
    if (this.recipe?.id) {
      this.likeService.getLikesCount(this.recipe.id).subscribe({
        next: (count) => {
          this.likesCount = count || 0;
          this.cdr.markForCheck();
        },
        error: () => {},
      });

      const user = this.authService.currentUser();
      if (user?.id) {
        this.likeService.hasUserLiked(user.id, this.recipe.id).subscribe({
          next: (liked) => {
            this.isLiked = liked;
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      }
    }
  }

  toggleLike(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    const user = this.authService.currentUser();
    if (!user) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: `/recipes` },
      });
      return;
    }

    if (this.isLiking || !this.recipe?.id) return;

    this.isLiking = true;
    this.cdr.markForCheck();

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

  goToAuthor(event: Event, authorId?: number): void {
    event.preventDefault();
    event.stopPropagation();
    if (authorId) {
      this.router.navigate(['/users', authorId]);
    }
  }

  get images(): string[] {
    return this.recipe?.images || [];
  }

  get hasMultipleImages(): boolean {
    return this.images.length > 1;
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

  prevImage(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.currentImageIndex > 0) {
      this.currentImageIndex--;
      this.cdr.markForCheck();
    }
  }

  nextImage(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.currentImageIndex < this.images.length - 1) {
      this.currentImageIndex++;
      this.cdr.markForCheck();
    }
  }

  goToImage(index: number, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (index >= 0 && index < this.images.length) {
      this.currentImageIndex = index;
      this.cdr.markForCheck();
    }
  }

  onTouchStart(event: TouchEvent): void {
    if (event.touches.length === 1) {
      this.touchStartX = event.touches[0].clientX;
    }
  }

  onTouchEnd(event: TouchEvent): void {
    if (!this.hasMultipleImages || event.changedTouches.length === 0) return;
    const touchEndX = event.changedTouches[0].clientX;
    const diff = this.touchStartX - touchEndX;

    if (Math.abs(diff) > 40) {
      if (diff > 0 && this.currentImageIndex < this.images.length - 1) {
        // Swiped left -> next
        this.currentImageIndex++;
        this.cdr.markForCheck();
      } else if (diff < 0 && this.currentImageIndex > 0) {
        // Swiped right -> prev
        this.currentImageIndex--;
        this.cdr.markForCheck();
      }
    }
  }
}
