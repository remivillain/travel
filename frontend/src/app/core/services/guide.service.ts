import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { firstValueFrom, catchError, of } from 'rxjs';
import { Guide } from '../models/guide.model';
import { GuideActivite } from '../models/guide-activite.model';
import { NetworkService } from './network.service';
import { OfflineStorageService } from './offline-storage.service';
import { SyncService } from './sync.service';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class GuideService {
  private apiUrl = environment.apiUrl + '/guides';

  constructor(
    private http: HttpClient,
    private networkService: NetworkService,
    private offlineStorage: OfflineStorageService,
    private syncService: SyncService,
    private authService: AuthService
  ) {}

  private getAuthHeaders() {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  async getUserGuides(): Promise<Guide[]> {
    // Côté serveur, retourner un tableau vide pour éviter les requêtes HTTP
    if (typeof window === 'undefined') {
      return [];
    }

    // Vérifier si l'utilisateur est admin
    const isAdmin = this.authService.hasRole('ADMIN');
    const endpoint = isAdmin ? this.apiUrl : this.apiUrl + '/mes-guides';

    // Si hors-ligne, retourner les données en cache (déjà filtrées par le backend)
    if (!this.networkService.isOnline()) {
      const cachedGuides = this.offlineStorage.getCachedUserGuides();
      if (cachedGuides) {
        console.log('📦 Guides chargés depuis le cache (hors-ligne)');
        return cachedGuides;
      }
      throw new Error('Aucune donnée disponible hors-ligne');
    }

    try {
      // Tentative de récupération en ligne
      const result = await firstValueFrom(
        this.http.get<Guide[]>(endpoint, { 
          headers: this.getAuthHeaders() 
        }).pipe(
          catchError(error => {
            // En cas d'erreur réseau, essayer le cache
            const cachedGuides = this.offlineStorage.getCachedUserGuides();
            if (cachedGuides) {
              console.log('📦 Guides chargés depuis le cache (erreur réseau)');
              return of(cachedGuides);
            }
            throw error;
          })
        )
      );

      const guides = result ?? [];
      
      // Le backend fait déjà le filtrage, pas besoin de refiltrer
      // Toujours mettre en cache les guides récupérés (même si vide)
      this.offlineStorage.cacheUserGuides(guides);
      console.log(`📦 ${guides.length} guides mis en cache`);

      return guides;
    } catch (error) {
      // Dernière tentative avec le cache
      const cachedGuides = this.offlineStorage.getCachedUserGuides();
      if (cachedGuides) {
        console.log('📦 Guides chargés depuis le cache (fallback)');
        return cachedGuides;
      }
      console.error('❌ Aucune donnée disponible (ni en ligne ni en cache):', error);
      throw error;
    }
  }

  async getGuide(id: number): Promise<Guide> {
    // Côté serveur, retourner un guide vide pour éviter les requêtes HTTP
    if (typeof window === 'undefined') {
      return { 
        id, 
        titre: '', 
        description: '', 
        nombreJours: 0,
        mobilites: [],
        saisons: [],
        pourQui: [],
        guideActivites: [],
        invitedUserIds: []
      } as Guide;
    }

    // Si hors-ligne, essayer le cache d'abord
    if (!this.networkService.isOnline()) {
      const cachedGuide = this.offlineStorage.getCachedGuide(id);
      if (cachedGuide) {
        console.log(`📦 Guide ${id} chargé depuis le cache (hors-ligne)`);
        return cachedGuide;
      }
      throw new Error('Guide non disponible hors-ligne');
    }

    try {
      // Tentative de récupération en ligne
      const guide = await firstValueFrom(
        this.http.get<Guide>(`${this.apiUrl}/${id}`, { 
          headers: this.getAuthHeaders() 
        }).pipe(
          catchError(error => {
            // Pour les erreurs d'autorisation/authentification, ne pas utiliser le cache
            if (error.status === 401 || error.status === 403 || error.status === 404) {
              throw error;
            }
            
            // En cas d'autres erreurs réseau, essayer le cache
            const cachedGuide = this.offlineStorage.getCachedGuide(id);
            if (cachedGuide) {
              console.log(`📦 Guide ${id} chargé depuis le cache (erreur réseau)`);
              return of(cachedGuide);
            }
            throw error;
          })
        )
      );

      // Le backend gère déjà les autorisations, pas besoin de vérifier côté frontend
      // Mettre en cache le guide récupéré
      this.offlineStorage.cacheGuide(guide);
      return guide;
    } catch (error: any) {
      // Pour les erreurs d'autorisation/authentification, ne pas utiliser le cache
      if (error.status === 401 || error.status === 403 || error.status === 404) {
        throw error;
      }
      
      // Dernière tentative avec le cache pour les autres erreurs
      const cachedGuide = this.offlineStorage.getCachedGuide(id);
      if (cachedGuide) {
        console.log(`📦 Guide ${id} chargé depuis le cache (fallback)`);
        return cachedGuide;
      }
      throw error;
    }
  }

  async toggleFavorite(id: number): Promise<void> {
    if (!this.networkService.isOnline()) {
      // En mode hors-ligne, ajouter à la queue de synchronisation
      this.syncService.queueUpdate(`/guides/${id}/favorite`, {});
      
      // Mettre à jour le cache local si possible
      const cachedGuide = this.offlineStorage.getCachedGuide(id);
      if (cachedGuide) {
        // Toggle local pour feedback immédiat
        // cachedGuide.isFavorite = !cachedGuide.isFavorite;
        this.offlineStorage.cacheGuide(cachedGuide);
      }
      
      console.log(`⭐ Favori pour le guide ${id} sera synchronisé plus tard`);
      return;
    }

    try {
      await firstValueFrom(
        this.http.post<void>(`${this.apiUrl}/${id}/favorite`, {}, { 
          headers: this.getAuthHeaders() 
        })
      );
      
      // Invalider le cache pour ce guide pour forcer un refresh
      this.offlineStorage.removeCache(`guide_${id}`);
    } catch (error) {
      // Si erreur réseau, ajouter à la queue de sync
      this.syncService.queueUpdate(`/guides/${id}/favorite`, {});
      console.log(`⭐ Favori pour le guide ${id} ajouté à la queue de synchronisation`);
    }
  }

}
