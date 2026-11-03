import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { RecipeService } from '../../../recipes/services/recipe.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ImageService } from '../../../../core/services/image.service';
import { UserResponse, UserSummary } from '../../../../shared/models/user';
import { Recipe } from '../../../../shared/models/recipe';
import { RecipeCard } from '../../../recipes/components/recipe-card/recipe-card';

@Component({
  selector: 'app-user-profile-page',
  imports: [RouterLink, DatePipe, ReactiveFormsModule, RecipeCard],
  templateUrl: './user-profile-page.html',
  styleUrl: './user-profile-page.css',
})
export class UserProfilePage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  userService = inject(UserService);
  authService = inject(AuthService);
  imageService = inject(ImageService);
  private recipeService = inject(RecipeService);
  private fb = inject(FormBuilder);

  userId!: number;
  user?: UserResponse;
  recipes: Recipe[] = [];
  followers: UserSummary[] = [];
  following: UserSummary[] = [];

  activeTab: 'recipes' | 'followers' | 'following' | 'edit' = 'recipes';
  isLoading = true;
  errorMessage = '';
  editMessage = '';
  editErrorMessage = '';
  isEditing = false;
  isFollowLoading = false;

  showPassword = false;
  editAvatarUrl = '';
  isUploadingAvatar = false;

  editForm = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (id) {
        this.userId = id;
        this.loadAllUserData(id);
      }
    });
  }

  setTab(tab: 'recipes' | 'followers' | 'following' | 'edit'): void {
    this.activeTab = tab;
    this.cdr.markForCheck();
  }

  loadAllUserData(id: number): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.userService.getUserById(id).subscribe({
      next: (u) => {
        this.user = u;
        this.editAvatarUrl = u.avatarUrl || '';
        this.editForm.patchValue({
          username: u.username,
          email: u.email,
          password: '',
        });
        this.isLoading = false;
        this.cdr.markForCheck();
        this.loadRecipes();
        this.loadFollows();
      },
      error: (err) => {
        console.error('Error loading user profile:', err);
        this.errorMessage = 'Impossible de charger ce profil utilisateur.';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadRecipes(): void {
    this.recipeService.getUserRecipes(this.userId).subscribe({
      next: (list) => {
        this.recipes = list || [];
        this.cdr.markForCheck();
      },
      error: (e) => console.warn('Could not load user recipes', e),
    });
  }

  loadFollows(): void {
    this.userService.getFollowers(this.userId).subscribe({
      next: (list) => {
        this.followers = list || [];
        this.cdr.markForCheck();
      },
      error: (e) => console.warn('Could not load followers', e),
    });

    this.userService.getFollowing(this.userId).subscribe({
      next: (list) => {
        this.following = list || [];
        this.cdr.markForCheck();
      },
      error: (e) => console.warn('Could not load following', e),
    });
  }

  get isSelf(): boolean {
    const current = this.authService.currentUser() || this.userService.activeUser();
    return current?.id === this.userId;
  }

  get userAvatarUrl(): string {
    return this.user?.avatarUrl ? this.imageService.getImageUrl(this.user.avatarUrl) : '';
  }

  get previewEditAvatarUrl(): string {
    return this.editAvatarUrl ? this.imageService.getImageUrl(this.editAvatarUrl) : '';
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    this.cdr.markForCheck();
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.isUploadingAvatar = true;
    this.editMessage = '';
    this.editErrorMessage = '';
    this.cdr.markForCheck();

    this.imageService.uploadImage(file).subscribe({
      next: (res) => {
        this.editAvatarUrl = res.url;
        this.isUploadingAvatar = false;

        // Auto-persist avatar directly to user profile
        const raw = this.editForm.getRawValue();
        const payload = {
          username: raw.username.trim() || this.user?.username || '',
          email: raw.email.trim() || this.user?.email || '',
          avatarUrl: res.url,
        };

        this.userService.updateProfile(this.userId, payload).subscribe({
          next: (updated) => {
            this.user = updated;
            if (this.isSelf) {
              this.authService.updateCurrentUser(updated);
            }
            this.editMessage = 'Photo de profil mise à jour avec succès !';
            this.cdr.markForCheck();
          },
          error: (err) => {
            console.warn('Could not auto-save avatar to profile:', err);
            this.editMessage = 'Photo importée. Cliquez sur "Enregistrer" pour valider.';
            this.cdr.markForCheck();
          },
        });

        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error uploading avatar:', err);
        this.isUploadingAvatar = false;
        this.editErrorMessage = 'Échec du téléversement de la photo de profil.';
        this.cdr.markForCheck();
      },
    });

    input.value = '';
  }

  removeAvatar(): void {
    this.editAvatarUrl = '';
    const raw = this.editForm.getRawValue();
    const payload = {
      username: raw.username.trim() || this.user?.username || '',
      email: raw.email.trim() || this.user?.email || '',
      avatarUrl: '',
    };

    this.userService.updateProfile(this.userId, payload).subscribe({
      next: (updated) => {
        this.user = updated;
        if (this.isSelf) {
          this.authService.updateCurrentUser(updated);
        }
        this.editMessage = 'Photo de profil retirée avec succès.';
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.warn('Could not remove avatar:', err);
        this.cdr.markForCheck();
      },
    });
  }

  get isFollowingThisUser(): boolean {
    const myId = this.userService.activeUser()?.id;
    if (!myId) return false;
    return this.followers.some((f) => f.id === myId);
  }

  toggleFollow(): void {
    const currentActive = this.userService.activeUser();
    if (!currentActive) {
      alert('Veuillez vous inscrire ou définir un profil actif pour suivre ce chef.');
      return;
    }

    if (this.isSelf) return;

    this.isFollowLoading = true;
    this.cdr.markForCheck();

    if (this.isFollowingThisUser) {
      this.userService.unfollowUser(currentActive.id, this.userId).subscribe({
        next: () => {
          this.isFollowLoading = false;
          this.loadFollows();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Unfollow error:', err);
          this.isFollowLoading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.userService.followUser(currentActive.id, this.userId).subscribe({
        next: () => {
          this.isFollowLoading = false;
          this.loadFollows();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Follow error:', err);
          this.isFollowLoading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  setAsActiveProfile(): void {
    if (this.user) {
      this.userService.setActiveUser(this.user);
      this.cdr.markForCheck();
    }
  }

  onUpdateProfile(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.isEditing = true;
    this.editMessage = '';
    this.editErrorMessage = '';
    this.cdr.markForCheck();

    const raw = this.editForm.getRawValue();
    const payload: { username: string; email: string; password?: string; avatarUrl?: string } = {
      username: raw.username.trim(),
      email: raw.email.trim(),
      avatarUrl: this.editAvatarUrl,
    };
    if (raw.password.trim().length >= 8) {
      payload.password = raw.password.trim();
    }

    this.userService.updateProfile(this.userId, payload).subscribe({
      next: (updated) => {
        this.isEditing = false;
        this.user = updated;
        if (this.isSelf) {
          this.authService.updateCurrentUser(updated);
        }
        this.editMessage = 'Profil mis à jour avec succès !';
        this.activeTab = 'recipes';
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Update profile error:', err);
        this.isEditing = false;
        this.editErrorMessage =
          err.error?.message || 'Erreur lors de la mise à jour des informations.';
        this.cdr.markForCheck();
      },
    });
  }
}
