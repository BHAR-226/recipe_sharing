import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  readonly query = signal<string>('');

  setQuery(query: string): void {
    this.query.set(query);
  }

  clear(): void {
    this.query.set('');
  }
}

