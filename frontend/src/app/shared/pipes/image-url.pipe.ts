import { Pipe, PipeTransform } from '@angular/core';

import { API_ORIGIN } from '../api';

@Pipe({
  name: 'imageUrl',
})
export class ImageUrlPipe implements PipeTransform {
  transform(path: string | null | undefined): string | null {
    if (!path) {
      return null;
    }

    const trimmed = path.trim();
    if (trimmed === '') {
      return null;
    }

    if (/^(https?:)?\/\//i.test(trimmed) || trimmed.startsWith('data:')) {
      return trimmed;
    }

    return trimmed.startsWith('/')
      ? `${API_ORIGIN}${trimmed}`
      : `${API_ORIGIN}/${trimmed}`;
  }
}
