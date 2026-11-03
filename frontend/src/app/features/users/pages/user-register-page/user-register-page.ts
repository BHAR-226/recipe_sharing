import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { ImageService } from '../../../../core/services/image.service';

@Component({
  selector: 'app-user-register-page',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './user-register-page.html',
  styleUrl: './user-register-page.css',
})
export class UserRegisterPage {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private imageService = inject(ImageService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  showPassword = false;

  avatarUrl = '';
  isUploadingAvatar = false;

  registerForm = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    this.cdr.markForCheck();
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.isUploadingAvatar = true;
    this.cdr.markForCheck();

    this.imageService.uploadImage(file).subscribe({
      next: (res) => {
        this.avatarUrl = res.url;
        this.isUploadingAvatar = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error uploading avatar:', err);
        this.isUploadingAvatar = false;
        this.cdr.markForCheck();
      },
    });
  }

  get fullAvatarUrl(): string {
    return this.avatarUrl ? this.imageService.getImageUrl(this.avatarUrl) : '';
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }

    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.cdr.markForCheck();

    const raw = this.registerForm.getRawValue();

    this.authService
      .register({
        username: raw.username.trim(),
        email: raw.email.trim(),
        password: raw.password,
        avatarUrl: this.avatarUrl || undefined,
      })
      .subscribe({
        next: (authRes) => {
          this.isSubmitting = false;
          this.successMessage = `Bienvenue sur RecipeNet, @${authRes.user.username} ! Votre compte chef est prêt.`;
          this.cdr.markForCheck();
          setTimeout(() => {
            this.router.navigate(['/recipes']);
          }, 1200);
        },
        error: (err) => {
          console.error('Registration failed:', err);
          this.isSubmitting = false;
          if (err.status === 409 || err.error?.message?.includes('already')) {
            this.errorMessage = err.error?.message || 'Ce pseudo ou cette adresse email est déjà utilisé(e).';
          } else {
            this.errorMessage = 'Une erreur est survenue lors de la création du compte. Vérifiez les informations.';
          }
          this.cdr.markForCheck();
        },
      });
  }
}
