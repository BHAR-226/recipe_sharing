import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login-page',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isSubmitting = false;
  errorMessage = '';
  returnUrl = '/recipes';
  showPassword = false;

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  loginForm = this.fb.nonNullable.group({
    login: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/recipes';

    if (this.authService.isLoggedIn()) {
      this.router.navigateByUrl(this.returnUrl);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const raw = this.loginForm.getRawValue();

    this.authService
      .login({
        login: raw.login.trim(),
        password: raw.password,
      })
      .subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigateByUrl(this.returnUrl);
        },
        error: (err) => {
          console.error('Login error:', err);
          this.isSubmitting = false;
          if (err.status === 401) {
            this.errorMessage = 'Identifiants incorrects. Veuillez vérifier votre pseudo/email et mot de passe.';
          } else {
            this.errorMessage = 'Impossible de se connecter au serveur pour le moment.';
          }
        },
      });
  }
}

