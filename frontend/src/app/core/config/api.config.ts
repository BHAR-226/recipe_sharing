import { environment } from '../../../environments/environment';

export function getApiBaseUrl(): string {
  // 1. Si une URL de production est configurée dans l'environnement, on l'utilise
  if (environment.apiUrl && environment.apiUrl.trim().length > 0) {
    return environment.apiUrl.trim().replace(/\/+$/, '');
  }

  // 2. Détection dynamique en développement (sur PC ou smartphone via Wi-Fi)
  if (typeof window !== 'undefined' && window.location && window.location.hostname) {
    return `http://${window.location.hostname}:8080/api`;
  }

  return 'http://localhost:8080/api';
}
