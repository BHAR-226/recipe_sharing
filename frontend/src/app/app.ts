import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './core/services/auth.service';
import { ImageService } from './core/services/image.service';
import { SearchService } from './core/services/search.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  authService = inject(AuthService);
  imageService = inject(ImageService);
  searchService = inject(SearchService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  currentYear = new Date().getFullYear();
  isMobileMenuOpen = false;

  ngOnInit(): void {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.isMobileMenuOpen = false;
        this.cdr.markForCheck();
      });
  }

  onSearchInput(event: Event): void {
    const val = (event.target as HTMLInputElement).value;
    this.searchService.setQuery(val);
    if (!this.router.url.startsWith('/recipes')) {
      this.router.navigate(['/recipes']);
    }
  }

  onSearchClear(): void {
    this.searchService.clear();
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    this.cdr.markForCheck();
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
    this.cdr.markForCheck();
  }

  getAvatarUrl(path?: string): string {
    return path ? this.imageService.getImageUrl(path) : '';
  }
}
