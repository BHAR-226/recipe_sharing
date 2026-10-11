import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  inject,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RecipeUpdateRequest } from '../../../../shared/models/recipe-update-request';

export interface RecipeFormResult {
  request: RecipeUpdateRequest;
  file: File | null;
}

@Component({
  selector: 'app-recipe-form',
  imports: [ReactiveFormsModule],
  templateUrl: './recipe-form.html',
  styleUrl: './recipe-form.css',
})
export class RecipeFormComponent implements OnDestroy {
  private fb = inject(FormBuilder);

  private static readonly ALLOWED_TYPES = [
    'image/jpeg',
    'image/png',
    'image/webp',
    'image/gif',
  ];
  private static readonly MAX_SIZE = 5 * 1024 * 1024;

  @Input() submitLabel = 'Save recipe';
  @Output() submitted = new EventEmitter<RecipeFormResult>();

  recipeForm = this.fb.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    ingredients: [''],
    steps: [''],
  });

  selectedFile: File | null = null;
  imagePreview: string | null = null;
  imageError = '';

  @Input()
  set initialValues(value: RecipeUpdateRequest | null) {
    if (value) {
      this.recipeForm.patchValue(value);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.imageError = '';

    const file = input.files?.[0] ?? null;
    if (!file) {
      this.clearImage();
      return;
    }

    if (!RecipeFormComponent.ALLOWED_TYPES.includes(file.type)) {
      this.imageError = 'Only JPEG, PNG, WEBP or GIF images are allowed.';
      input.value = '';
      return;
    }

    if (file.size > RecipeFormComponent.MAX_SIZE) {
      this.imageError = 'Image must be 5MB or smaller.';
      input.value = '';
      return;
    }

    this.revokePreview();
    this.selectedFile = file;
    this.imagePreview = URL.createObjectURL(file);
  }

  clearImage(): void {
    this.revokePreview();
    this.selectedFile = null;
    this.imagePreview = null;
    this.imageError = '';
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) {
      this.recipeForm.markAllAsTouched();
      return;
    }

    this.submitted.emit({
      request: this.recipeForm.getRawValue(),
      file: this.selectedFile,
    });
  }

  ngOnDestroy(): void {
    this.revokePreview();
  }

  private revokePreview(): void {
    if (this.imagePreview) {
      URL.revokeObjectURL(this.imagePreview);
    }
  }
}
