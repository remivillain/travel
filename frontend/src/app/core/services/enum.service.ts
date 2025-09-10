import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EnumService {
  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  getMobilites() {
    return this.http.get<string[]>(environment.apiUrl + '/enums/mobilite', {
      headers: this.getAuthHeaders()
    });
  }
  getSaisons() {
    return this.http.get<string[]>(environment.apiUrl + '/enums/saison', {
      headers: this.getAuthHeaders()
    });
  }
  getPourQui() {
    return this.http.get<string[]>(environment.apiUrl + '/enums/pourqui', {
      headers: this.getAuthHeaders()
    });
  }
}
