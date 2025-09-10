import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';
import { Guide } from '../models/guide.model';

@Injectable({ providedIn: 'root' })
export class GuideService {
  private apiUrl = environment.apiUrl + '/guides/mes-guides';

  constructor(private http: HttpClient) {}

  async getUserGuides(): Promise<Guide[]> {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    const headers = token ? { Authorization: `Bearer ${token}` } : undefined;
    const result = await firstValueFrom(this.http.get<Guide[]>(this.apiUrl, { headers }));
    return result ?? [];
  }
}
