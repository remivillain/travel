import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, catchError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { NetworkService } from './network.service';
import { OfflineStorageService } from './offline-storage.service';

@Injectable({ providedIn: 'root' })
export class EnumService {
  constructor(
    private http: HttpClient,
    private networkService: NetworkService,
    private offlineStorage: OfflineStorageService
  ) {}

  private getAuthHeaders() {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  getMobilites(): Observable<string[]> {
    return this.getEnumWithCache('mobilites', '/enums/mobilite');
  }

  getSaisons(): Observable<string[]> {
    return this.getEnumWithCache('saisons', '/enums/saison');
  }

  getPourQui(): Observable<string[]> {
    return this.getEnumWithCache('pourqui', '/enums/pourqui');
  }

  private getEnumWithCache(cacheKey: string, endpoint: string): Observable<string[]> {
    // Côté serveur, retourner un tableau vide
    if (typeof window === 'undefined') {
      return of([]);
    }

    // Si hors-ligne, essayer le cache d'abord
    if (!this.networkService.isOnline()) {
      const cachedData = this.offlineStorage.getCache<string[]>(cacheKey);
      if (cachedData) {
        console.log(`📦 Enum ${cacheKey} chargé depuis le cache (hors-ligne)`);
        return of(cachedData);
      }
      console.warn(`⚠️ Aucune donnée en cache pour ${cacheKey}`);
      return of([]);
    }

    // En ligne : récupérer depuis l'API avec fallback vers le cache
    return this.http.get<string[]>(environment.apiUrl + endpoint, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap(data => {
        // Mettre en cache les données récupérées (24h de validité)
        this.offlineStorage.setCache(cacheKey, data, 1440);
        console.log(`📦 Enum ${cacheKey} mis en cache`);
      }),
      catchError(error => {
        // En cas d'erreur réseau, essayer le cache
        const cachedData = this.offlineStorage.getCache<string[]>(cacheKey);
        if (cachedData) {
          console.log(`📦 Enum ${cacheKey} récupéré depuis le cache (erreur réseau)`);
          return of(cachedData);
        }
        console.error(`❌ Erreur lors de la récupération de ${cacheKey}:`, error);
        return of([]);
      })
    );
  }
}
