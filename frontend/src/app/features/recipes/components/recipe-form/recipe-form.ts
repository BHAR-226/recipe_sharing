import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RecipeRequest } from '../../../../shared/models/recipe-request';
import { UserService } from '../../../users/services/user.service';
import { ImageService } from '../../../../core/services/image.service';

export interface RecipeFormData {
  title: string;
  description: string;
  ingredients: string;
  steps: string;
  images?: string[];
  authorId: number;
}

@Component({
  selector: 'app-recipe-form',
  imports: [ReactiveFormsModule],
  templateUrl: './recipe-form.html',
  styleUrl: './recipe-form.css',
})
export class RecipeFormComponent implements OnInit, OnChanges {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  public imageService = inject(ImageService);
  private cdr = inject(ChangeDetectorRef);

  @Input() initialData?: {
    title?: string;
    description?: string;
    ingredients?: string;
    steps?: string;
    images?: string[];
    authorId?: number;
  } | null = null;

  @Input() submitLabel = 'Enregistrer la recette';
  @Input() isSubmitting = false;
  @Input() defaultAuthorId = 1;

  @Output() submitted = new EventEmitter<RecipeRequest>();

  images: string[] = [];
  isUploadingImages = false;
  uploadError = '';

  recipeForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    ingredients: [''],
    steps: [''],
    authorId: [1],
  });

  ngOnInit(): void {
    const currentId = this.userService.getActiveUserId();
    this.recipeForm.patchValue({
      authorId: currentId || this.defaultAuthorId,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initialData'] && this.initialData) {
      this.recipeForm.patchValue({
        title: this.initialData.title || '',
        description: this.initialData.description || '',
        ingredients: this.initialData.ingredients || '',
        steps: this.initialData.steps || '',
        authorId:
          this.initialData.authorId ||
          this.userService.getActiveUserId() ||
          this.defaultAuthorId,
      });

      if (this.initialData.images && Array.isArray(this.initialData.images)) {
        this.images = [...this.initialData.images];
      }
    }
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const filesArray = Array.from(input.files);
    this.uploadError = '';
    this.isUploadingImages = true;

    this.imageService.uploadMultiple(filesArray).subscribe({
      next: (responses) => {
        const newUrls = responses.map((r) => r.url);
        this.images = [...this.images, ...newUrls];
        this.isUploadingImages = false;
        input.value = '';
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error uploading images:', err);
        this.uploadError = "Échec du téléchargement d'une ou plusieurs photos.";
        this.isUploadingImages = false;
        input.value = '';
        this.cdr.markForCheck();
      },
    });
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
    this.cdr.markForCheck();
  }

  makePrimary(index: number): void {
    if (index > 0 && index < this.images.length) {
      const [item] = this.images.splice(index, 1);
      this.images.unshift(item);
      this.cdr.markForCheck();
    }
  }

  moveLeft(index: number): void {
    if (index > 0) {
      const temp = this.images[index];
      this.images[index] = this.images[index - 1];
      this.images[index - 1] = temp;
      this.cdr.markForCheck();
    }
  }

  moveRight(index: number): void {
    if (index < this.images.length - 1) {
      const temp = this.images[index];
      this.images[index] = this.images[index + 1];
      this.images[index + 1] = temp;
      this.cdr.markForCheck();
    }
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) {
      this.recipeForm.markAllAsTouched();
      return;
    }

    const raw = this.recipeForm.getRawValue();
    const effectiveAuthorId =
      raw.authorId || this.userService.getActiveUserId() || this.defaultAuthorId;

    this.submitted.emit({
      title: raw.title.trim(),
      description: raw.description?.trim() || '',
      ingredients: raw.ingredients.trim(),
      steps: raw.steps.trim(),
      images: this.images,
      authorId: effectiveAuthorId,
    });
  }
}
