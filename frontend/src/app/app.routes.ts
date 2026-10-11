import { Routes } from '@angular/router';
import { RecipeListPage } from './features/recipes/pages/recipe-list-page/recipe-list-page';
import { RecipeDetailPage } from './features/recipes/pages/recipe-detail-page/recipe-detail-page';
import {RecipeCreatePage} from './features/recipes/pages/recipe-create-page/recipe-create-page';
import { RecipeEditPage } from './features/recipes/pages/recipe-edit-page/recipe-edit-page';
import { LoginPage } from './features/auth/pages/login-page/login-page';
import { RegisterPage } from './features/auth/pages/register-page/register-page';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPage
  },
  {
    path: 'register',
    component: RegisterPage
  },
  {
    path: 'recipes/create',
    component : RecipeCreatePage,
    canActivate: [authGuard]
  },
  {
  path: 'recipes/:id/edit',
  component: RecipeEditPage,
  canActivate: [authGuard]
  },
  {
    path: 'recipes/:id',
    component: RecipeDetailPage,
    canActivate: [authGuard]
  },
  {
    path: 'recipes',
    component: RecipeListPage,
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: 'recipes',
    pathMatch: 'full'
  }
];
