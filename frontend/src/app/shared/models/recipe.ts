export interface UserSummary {
  id: number;
  username: string;
  avatarUrl?: string;
}

export interface Recipe {
  id: number;
  title: string;
  description: string;
  ingredients: string;
  steps: string;
  images: string[];
  author: UserSummary;
  createdAt: string;
  updatedAt: string;
}
