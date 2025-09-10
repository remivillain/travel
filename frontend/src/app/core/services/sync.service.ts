import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NetworkService } from './network.service';
import { OfflineStorageService, PendingSync } from './offline-storage.service';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';

export interface SyncStatus {
  isActive: boolean;
  pendingCount: number;
  lastSync: Date | null;
  errors: string[];
}

@Injectable({
  providedIn: 'root'
})
export class SyncService {
  private readonly syncStatus = signal<SyncStatus>({
    isActive: false,
    pendingCount: 0,
    lastSync: null,
    errors: []
  });

  private syncInProgress = false;
  private syncInterval?: number;

  constructor(
    private http: HttpClient,
    private networkService: NetworkService,
    private offlineStorage: OfflineStorageService
  ) {
    this.initializeSync();
  }

  get status() {
    return this.syncStatus.asReadonly();
  }

  private initializeSync(): void {
    // Ne pas initialiser côté serveur
    if (typeof window === 'undefined') {
      return;
    }

    // Synchroniser quand la connexion revient
    this.networkService.isOnline$.subscribe(isOnline => {
      if (isOnline && !this.syncInProgress) {
        setTimeout(() => this.syncPendingActions(), 1000);
      }
    });

    // Synchronisation périodique quand en ligne
    this.startPeriodicSync();
    
    // Mettre à jour le compteur initial
    this.updatePendingCount();
  }

  private startPeriodicSync(): void {
    if (typeof window === 'undefined') return;

    // Synchroniser toutes les 5 minutes si en ligne
    this.syncInterval = window.setInterval(() => {
      if (this.networkService.isOnline() && !this.syncInProgress) {
        this.syncPendingActions();
      }
    }, 5 * 60 * 1000);
  }

  /**
   * Synchronise toutes les actions en attente
   */
  async syncPendingActions(): Promise<void> {
    if (this.syncInProgress || !this.networkService.isOnline()) {
      return;
    }

    this.syncInProgress = true;
    this.updateSyncStatus({ isActive: true, errors: [] });

    try {
      const queue = this.offlineStorage.getSyncQueue();
      console.log(`🔄 Début de synchronisation: ${queue.length} actions en attente`);

      if (queue.length === 0) {
        this.updateSyncStatus({ 
          isActive: false, 
          lastSync: new Date(),
          pendingCount: 0 
        });
        return;
      }

      const errors: string[] = [];
      let successCount = 0;

      // Traiter chaque action de la queue
      for (const action of queue) {
        try {
          await this.processAction(action);
          this.offlineStorage.removeFromSyncQueue(action.id);
          successCount++;
          console.log(`✅ Action synchronisée: ${action.type} ${action.endpoint}`);
        } catch (error) {
          console.error(`❌ Erreur lors de la sync de l'action ${action.id}:`, error);
          errors.push(`${action.type} ${action.endpoint}: ${error}`);
          
          // Supprimer les actions trop anciennes (plus de 24h)
          if (Date.now() - action.timestamp > 24 * 60 * 60 * 1000) {
            this.offlineStorage.removeFromSyncQueue(action.id);
            console.log(`🗑️ Action expirée supprimée: ${action.id}`);
          }
        }
      }

      const remainingCount = this.offlineStorage.getSyncQueue().length;
      
      this.updateSyncStatus({
        isActive: false,
        lastSync: new Date(),
        pendingCount: remainingCount,
        errors
      });

      if (successCount > 0) {
        console.log(`🎉 Synchronisation terminée: ${successCount} actions réussies, ${errors.length} erreurs`);
      }

    } catch (error) {
      console.error('❌ Erreur générale de synchronisation:', error);
      this.updateSyncStatus({
        isActive: false,
        errors: [`Erreur de synchronisation: ${error}`]
      });
    } finally {
      this.syncInProgress = false;
    }
  }

  /**
   * Traite une action de synchronisation individuelle
   */
  private async processAction(action: PendingSync): Promise<void> {
    const headers = this.getAuthHeaders();
    const url = environment.apiUrl + action.endpoint;

    switch (action.type) {
      case 'CREATE':
        await firstValueFrom(this.http.post(url, action.data, { headers }));
        break;
      
      case 'UPDATE':
        await firstValueFrom(this.http.put(url, action.data, { headers }));
        break;
      
      case 'DELETE':
        await firstValueFrom(this.http.delete(url, { headers }));
        break;
      
      default:
        throw new Error(`Type d'action non supporté: ${action.type}`);
    }
  }

  /**
   * Ajoute une action de synchronisation pour création
   */
  queueCreate(endpoint: string, data: any): void {
    this.offlineStorage.addToSyncQueue({
      type: 'CREATE',
      endpoint,
      data
    });
    this.updatePendingCount();
  }

  /**
   * Ajoute une action de synchronisation pour mise à jour
   */
  queueUpdate(endpoint: string, data: any): void {
    this.offlineStorage.addToSyncQueue({
      type: 'UPDATE',
      endpoint,
      data
    });
    this.updatePendingCount();
  }

  /**
   * Ajoute une action de synchronisation pour suppression
   */
  queueDelete(endpoint: string): void {
    this.offlineStorage.addToSyncQueue({
      type: 'DELETE',
      endpoint
    });
    this.updatePendingCount();
  }

  /**
   * Force une synchronisation immédiate
   */
  async forcSync(): Promise<void> {
    if (!this.networkService.isOnline()) {
      throw new Error('Impossible de synchroniser: aucune connexion réseau');
    }
    
    await this.syncPendingActions();
  }

  /**
   * Vide la queue de synchronisation
   */
  clearSyncQueue(): void {
    this.offlineStorage.clearSyncQueue();
    this.updatePendingCount();
  }

  private updateSyncStatus(updates: Partial<SyncStatus>): void {
    this.syncStatus.update(current => ({
      ...current,
      ...updates
    }));
  }

  private updatePendingCount(): void {
    const count = this.offlineStorage.getSyncQueue().length;
    this.syncStatus.update(current => ({
      ...current,
      pendingCount: count
    }));
  }

  private getAuthHeaders() {
    const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;
    return token ? { Authorization: `Bearer ${token}` } : undefined;
  }

  ngOnDestroy(): void {
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
    }
  }
}
