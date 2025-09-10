import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';
import { Guide } from '../models/guide.model';
import { GuideActivite } from '../models/guide-activite.model';

@Injectable({ providedIn: 'root' })
export class GuideService {
  private apiUrl = environment.apiUrl + '/guides';

  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  async getUserGuides(): Promise<Guide[]> {
    const result = await firstValueFrom(this.http.get<Guide[]>(this.apiUrl + '/mes-guides', { headers: this.getAuthHeaders() }));
    return result ?? [];
  }

  async getGuide(id: number): Promise<Guide> {
    return await firstValueFrom(this.http.get<Guide>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() }));
  }

  async toggleFavorite(id: number): Promise<void> {
    await firstValueFrom(this.http.post<void>(`${this.apiUrl}/${id}/favorite`, {}, { headers: this.getAuthHeaders() }));
  }

}
