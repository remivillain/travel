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

  currentUser(): { id?: number, email?: string } | null {
    if (typeof window === 'undefined') return null;
    
    try {
      const token = localStorage.getItem('jwt');
      if (!token) return null;
      
      const payload = JSON.parse(atob(token.split('.')[1]));
      return { 
        id: payload.userId || payload.id,
        email: payload.email 
      };
    } catch {
      return null;
    }
  }

  getCurrentUserId(): number | null {
    const user = this.currentUser();
    return user?.id || null;
  }
}
