export interface UserSummary {
  id: number;
  username: string;
  profileImageUrl?: string | null;
}

export interface Recipe {
  id: number;
  title: string;
  description: string;
  ingredients: string;
  steps: string;
  imageUrl?: string | null;
  author: UserSummary;
  createdAt: string;
  updatedAt: string;
}