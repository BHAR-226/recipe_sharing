import { Component, OnInit, inject, ChangeDetectorRef, effect } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Recipe } from '../../../../shared/models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { SearchService } from '../../../../core/services/search.service';
import { RecipeCard } from '../../components/recipe-card/recipe-card';

@Component({
  selector: 'app-recipe-list-page',
  imports: [RouterLink, RecipeCard],
  templateUrl: './recipe-list-page.html',
  styleUrl: './recipe-list-page.css',
})
export class RecipeListPage implements OnInit {
  private recipeService = inject(RecipeService);
  public searchService = inject(SearchService);
  private cdr = inject(ChangeDetectorRef);

  recipes: Recipe[] = [];
  filteredRecipes: Recipe[] = [];
  searchQuery = '';
  isLoading = true;
  errorMessage = '';

  constructor() {
    effect(() => {
      this.searchQuery = this.searchService.query();
      this.applyFilter();
      this.cdr.markForCheck();
    });
  }

  ngOnInit(): void {
    this.fetchRecipes();
  }

  fetchRecipes(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.recipeService.getRecipes().subscribe({
      next: (data) => {
        this.recipes = data || [];
        this.applyFilter();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading recipes:', err);
        this.errorMessage = 'Impossible de charger les recettes pour le moment.';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilter(): void {
    if (!this.searchQuery.trim()) {
      this.filteredRecipes = [...this.recipes];
      return;
    }

    const q = this.searchQuery.toLowerCase().trim();
    this.filteredRecipes = this.recipes.filter(
      (r) =>
        r.title?.toLowerCase().includes(q) ||
        r.description?.toLowerCase().includes(q) ||
        r.ingredients?.toLowerCase().includes(q)
    );
  }

  clearSearch(): void {
    this.searchService.clear();
  }
}
