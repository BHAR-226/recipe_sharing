export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  profileImageUrl?: string | null;
  createdAt: string;
}
