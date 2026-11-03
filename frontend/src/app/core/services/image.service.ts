import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiBaseUrl } from '../config/api.config';

export interface ImageUploadResponse {
  url: string;
  filename: string;
}

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private http = inject(HttpClient);

  private get apiUrl(): string {
    return `${getApiBaseUrl()}/images`;
  }

  uploadImage(file: File): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImageUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  uploadMultiple(files: File[]): Observable<ImageUploadResponse[]> {
    const formData = new FormData();
    files.forEach((f) => formData.append('files', f));
    return this.http.post<ImageUploadResponse[]>(`${this.apiUrl}/upload-multiple`, formData);
  }

  getImageUrl(path?: string | null): string {
    if (!path) return '';
    if (
      path.startsWith('http://') ||
      path.startsWith('https://') ||
      path.startsWith('data:') ||
      path.startsWith('blob:')
    ) {
      return path;
    }
    const apiBase = getApiBaseUrl();
    const origin = apiBase.replace(/\/api\/?$/, '');
    const cleanPath = path.startsWith('/') ? path : '/' + path;
    return `${origin}${cleanPath}`;
  }
}

