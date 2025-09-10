import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth/login';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<string> {
    return this.http.post<{ token: string }>(this.apiUrl, { email, password })
      .pipe(map(res => res.token));
  }
  
  logout(): void {
    localStorage.removeItem('jwt');
  }

  currentUser(): { email?: string } | null {
    if (typeof window === 'undefined') return null;
    const token = localStorage.getItem('jwt');
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return { email: payload.email };
    } catch {
      return null;
    }
  }
}
