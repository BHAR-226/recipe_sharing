export function getApiBaseUrl(): string {
  if (typeof window !== 'undefined' && window.location && window.location.hostname) {
    return `http://${window.location.hostname}:8080/api`;
  }
  return 'http://localhost:8080/api';
}

