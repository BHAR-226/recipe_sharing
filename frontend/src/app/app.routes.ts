import { Routes } from '@angular/router';
import { RecipeListPage } from './features/recipes/pages/recipe-list-page/recipe-list-page';
import { RecipeDetailPage } from './features/recipes/pages/recipe-detail-page/recipe-detail-page';
import { RecipeCreatePage } from './features/recipes/pages/recipe-create-page/recipe-create-page';
import { RecipeEditPage } from './features/recipes/pages/recipe-edit-page/recipe-edit-page';
import { UserRegisterPage } from './features/users/pages/user-register-page/user-register-page';
import { UserProfilePage } from './features/users/pages/user-profile-page/user-profile-page';
import { UserListPage } from './features/users/pages/user-list-page/user-list-page';
import { LoginPage } from './features/auth/pages/login-page/login-page';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Recipes
  {
    path: 'recipes/create',
    component: RecipeCreatePage,
    title: 'Créer une recette | RecipeNet',
    canActivate: [authGuard],
  },
  {
    path: 'recipes/:id/edit',
    component: RecipeEditPage,
    title: 'Modifier la recette | RecipeNet',
    canActivate: [authGuard],
  },
  {
    path: 'recipes/:id',
    component: RecipeDetailPage,
    title: 'Détails de la recette | RecipeNet',
  },
  {
    path: 'recipes',
    component: RecipeListPage,
    title: 'Recettes Gastronomiques | RecipeNet',
  },

  // Auth & Users
  {
    path: 'login',
    component: LoginPage,
    title: 'Connexion Chef | RecipeNet',
  },
  {
    path: 'register',
    component: UserRegisterPage,
    title: 'Devenir Chef Membre | RecipeNet',
  },
  {
    path: 'community',
    component: UserListPage,
    title: 'Communauté des Chefs | RecipeNet',
  },
  {
    path: 'users/:id',
    component: UserProfilePage,
    title: 'Profil Chef | RecipeNet',
  },
  {
    path: 'users',
    redirectTo: 'community',
    pathMatch: 'full',
  },

  // Default redirect
  {
    path: '',
    redirectTo: 'recipes',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: 'recipes',
  },
];
