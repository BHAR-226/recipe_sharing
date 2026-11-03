export interface RecipeRequest {
  title: string;
  description: string;
  ingredients: string;
  steps: string;
  images?: string[];
  authorId: number;
}

export interface RecipeUpdateRequest {
  title: string;
  description: string;
  ingredients: string;
  steps: string;
  images?: string[];
}
