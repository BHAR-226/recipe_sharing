import { UserSummary } from './recipe';

export interface Comment {
  id: number;
  content: string;
  user: UserSummary;
  createdAt: string;
}