import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Recipe } from '../../../../shared/models/recipe';
import { ImageUrlPipe } from '../../../../shared/pipes/image-url.pipe';

@Component({
  selector: 'app-recipe-card',
  imports: [RouterLink, ImageUrlPipe],
  templateUrl: './recipe-card.html',
  styleUrl: './recipe-card.css',
})
export class RecipeCard {
  @Input({ required: true }) recipe!: Recipe;
  @Input() showAuthor = true;
}