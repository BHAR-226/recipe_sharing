import { Component, OnInit } from '@angular/core';
import { Recipe } from '../../../../shared/models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { RecipeCard } from '../../components/recipe-card/recipe-card';

@Component({
  selector: 'app-recipe-list-page',
  imports: [RecipeCard],
  templateUrl: './recipe-list-page.html',
  styleUrl: './recipe-list-page.css',
})
export class RecipeListPage implements OnInit {

  recipes: Recipe[] = [];

  constructor(private recipeService: RecipeService) {}

  ngOnInit(): void {
    this.recipeService.getRecipes().subscribe({
      next: (recipes) => {
        this.recipes = recipes;
      },
      error: (error) => {
        console.error('Error loading recipes:', error);
      }
    });
  }
}