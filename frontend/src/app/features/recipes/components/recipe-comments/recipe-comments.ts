
import { Component, Input, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RecipeService } from '../../services/recipe.service';
import { Comment } from '../../../../shared/models/comment';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-recipe-comments',
  imports: [FormsModule, DatePipe],
  templateUrl: './recipe-comments.html',
  styleUrl: './recipe-comments.css',
})
export class RecipeCommentsComponent implements OnInit {
  @Input({ required: true }) recipeId!: number;
  @Input({ required: true }) userId!: number;

  protected readonly auth = inject(AuthService);

  comments: Comment[] = [];
  newComment = '';
  loading = false;
  errorMessage = '';

  constructor(private recipeService: RecipeService) {}

  ngOnInit(): void {
    this.loadComments();
  }

  loadComments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.recipeService.getComments(this.recipeId).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load comments.';
        this.loading = false;
      },
    });
  }

  addComment(): void {
    const content = this.newComment.trim();

    if (!content || !this.userId) {
      return;
    }

    this.recipeService.addComment({
      content,
      recipeId: this.recipeId,
    }).subscribe({
      next: (comment) => {
        this.comments = [...this.comments, comment];
        this.newComment = '';
      },
      error: () => {
        this.errorMessage = 'Could not add your comment.';
      },
    });
  }

  deleteComment(commentId: number): void {
    this.recipeService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter(
          (comment) => comment.id !== commentId
        );
      },
      error: () => {
        this.errorMessage = 'Could not delete the comment.';
      },
    });
  }
}