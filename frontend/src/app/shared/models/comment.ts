import { UserSummary } from './user';

export interface CommentResponse {
  id: number;
  content: string;
  user: UserSummary;
  createdAt: string;
}

export interface CommentRequest {
  content: string;
  userId?: number;
  recipeId: number;
}

