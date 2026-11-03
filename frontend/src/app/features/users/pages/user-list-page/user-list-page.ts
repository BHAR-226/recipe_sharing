import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { UserService } from '../../services/user.service';
import { ImageService } from '../../../../core/services/image.service';
import { UserResponse } from '../../../../shared/models/user';

@Component({
  selector: 'app-user-list-page',
  imports: [RouterLink, FormsModule, DatePipe],
  templateUrl: './user-list-page.html',
  styleUrl: './user-list-page.css',
})
export class UserListPage implements OnInit {
  userService = inject(UserService);
  imageService = inject(ImageService);

  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
  searchQuery = '';
  isLoading = true;
  errorMessage = '';

  getChefAvatarUrl(avatarUrl?: string): string {
    return avatarUrl ? this.imageService.getImageUrl(avatarUrl) : '';
  }

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.userService.getUsers(0, 50).subscribe({
      next: (page) => {
        this.users = page.content || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading user list:', err);
        this.errorMessage = 'Impossible de charger la liste des membres.';
        this.isLoading = false;
      },
    });
  }

  applyFilter(): void {
    if (!this.searchQuery.trim()) {
      this.filteredUsers = [...this.users];
      return;
    }

    const q = this.searchQuery.toLowerCase().trim();
    this.filteredUsers = this.users.filter(
      (u) =>
        u.username.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q) ||
        (u.role && u.role.toLowerCase().includes(q))
    );
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.applyFilter();
  }

  makeActive(user: UserResponse, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.userService.setActiveUser(user);
  }
}

