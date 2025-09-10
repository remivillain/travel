import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Guide } from '../models/guide.model';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GuideService {
  private apiUrl = environment.apiUrl + '/guides/mes-guides';

  constructor(private http: HttpClient) {}
  async getUserGuides(): Promise<Guide[]> {
    const guides = await firstValueFrom(this.http.get<Guide[]>(this.apiUrl));
    return guides ?? [];
  }
  
}
