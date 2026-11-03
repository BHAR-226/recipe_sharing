export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: string;
  avatarUrl?: string;
  createdAt: string;
}

export interface UserRequest {
  username: string;
  email: string;
  password: string;
  avatarUrl?: string;
}

export interface UserProfileRequest {
  username: string;
  email: string;
  password?: string;
  avatarUrl?: string;
}

export interface UserSummary {
  id: number;
  username: string;
  avatarUrl?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
