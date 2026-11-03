import { HttpInterceptorFn } from '@angular/common/http';

import { API_ORIGIN } from '../../shared/api';

const SAFE_METHODS = ['GET', 'HEAD', 'OPTIONS'];
const CSRF_COOKIE = 'XSRF-TOKEN';
const CSRF_HEADER = 'X-XSRF-TOKEN';

function readCookie(name: string): string | null {
  if (typeof document === 'undefined') {
    return null;
  }

  const match = document.cookie.match(
    new RegExp('(?:^|;\\s*)' + name + '=([^;]*)')
  );

  return match ? decodeURIComponent(match[1]) : null;
}

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(API_ORIGIN)) {
    return next(req);
  }

  let headers = req.headers;

  if (!SAFE_METHODS.includes(req.method.toUpperCase())) {
    const token = readCookie(CSRF_COOKIE);
    if (token) {
      headers = headers.set(CSRF_HEADER, token);
    }
  }

  return next(req.clone({ withCredentials: true, headers }));
};
