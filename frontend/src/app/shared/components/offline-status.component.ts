import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkService } from '../../core/services/network.service';
import { SyncService } from '../../core/services/sync.service';
import { OfflineStorageService } from '../../core/services/offline-storage.service';

@Component({
  selector: 'app-offline-status',
  standalone: true,
  imports: [CommonModule],
  host: {
    '[class.show-bar]': 'shouldShowBar()'
  },
  template: `
    <!-- Barre de statut hors-ligne -->
    <div class="offline-status-bar" 
         [class.offline]="!isOnline()"
         [class.syncing]="syncStatus().isActive"
         [class.has-pending]="syncStatus().pendingCount > 0">
      
      <!-- Mode hors-ligne -->
      <div *ngIf="!isOnline()" class="status-content offline-content">
        <div class="flex items-center space-x-2">
          <svg class="w-4 h-4 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.5 0L4.268 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
          </svg>
          <span class="text-sm font-medium">Mode hors-ligne</span>
        </div>
        <p class="text-xs text-gray-600 mt-1">
          Les modifications seront synchronisées à la reconnexion
        </p>
      </div>

      <!-- Synchronisation en cours -->
      <div *ngIf="isOnline() && syncStatus().isActive" class="status-content syncing-content">
        <div class="flex items-center space-x-2">
          <div class="sync-spinner">
            <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
          </div>
          <span class="text-sm font-medium text-blue-700">Synchronisation...</span>
        </div>
      </div>

      <!-- Actions en attente -->
      <div *ngIf="isOnline() && !syncStatus().isActive && syncStatus().pendingCount > 0" 
           class="status-content pending-content">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-2">
            <svg class="w-4 h-4 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span class="text-sm font-medium text-yellow-700">
              {{ syncStatus().pendingCount }} action(s) en attente
            </span>
          </div>
          <button 
            (click)="forceSync()"
            [disabled]="syncStatus().isActive"
            class="btn-link-style text-xs text-blue-600 hover:text-blue-800">
            Synchroniser
          </button>
        </div>
      </div>

      <!-- Dernière synchronisation -->
      <div *ngIf="isOnline() && !syncStatus().isActive && syncStatus().pendingCount === 0 && syncStatus().lastSync" 
           class="status-content success-content">
        <div class="flex items-center space-x-2">
          <svg class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
          <span class="text-xs text-green-700">
            Synchronisé {{ formatLastSync(syncStatus().lastSync!) }}
          </span>
        </div>
      </div>

      <!-- Erreurs de synchronisation -->
      <div *ngIf="syncStatus().errors.length > 0" class="status-content error-content">
        <div class="flex items-center space-x-2">
          <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span class="text-xs text-red-700">
            {{ syncStatus().errors.length }} erreur(s) de synchronisation
          </span>
        </div>
      </div>
    </div>

    <!-- Cache info (dev mode) -->
    <div *ngIf="showCacheInfo" class="cache-info">
      <div class="text-xs text-gray-500 p-2 bg-gray-50 border-t">
        Cache: {{ cacheInfo.itemCount }} éléments ({{ formatBytes(cacheInfo.totalSize) }})
        <button (click)="clearCache()" class="ml-2 text-red-600 hover:text-red-800">
          Vider
        </button>
      </div>
    </div>
  `,
  styles: [`
    .offline-status-bar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
      background: #f8f9fa;
      border-bottom: 1px solid #e9ecef;
      transform: translateY(-100%);
      transition: all 0.3s ease-in-out;
    }

    .offline-status-bar.offline,
    .offline-status-bar.syncing,
    .offline-status-bar.has-pending {
      transform: translateY(0);
    }

    .offline-status-bar.offline {
      background: #fff3cd;
      border-bottom-color: #ffeaa7;
    }

    .offline-status-bar.syncing {
      background: #e7f3ff;
      border-bottom-color: #b3d9ff;
    }

    .offline-status-bar.has-pending {
      background: #fff8e1;
      border-bottom-color: #ffecb3;
    }

    .status-content {
      padding: 8px 16px;
      max-width: 1200px;
      margin: 0 auto;
    }

    /* Ajout de l'espace pour pousser le contenu vers le bas */
    :host {
      display: block;
      height: 0;
      transition: height 0.3s ease-in-out;
    }

    :host.show-bar {
      height: 60px;
    }

    .sync-spinner svg {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    .cache-info {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: 999;
    }
  `]
})
export class OfflineStatusComponent implements OnInit {
  showCacheInfo = false; // Mettre à true pour le debug
  cacheInfo: any = { itemCount: 0, totalSize: 0 };

  constructor(
    private networkService: NetworkService,
    private syncService: SyncService,
    private offlineStorage: OfflineStorageService
  ) {}

  ngOnInit() {
    this.updateCacheInfo();
    
    // Mise à jour périodique des infos de cache
    setInterval(() => {
      this.updateCacheInfo();
    }, 10000);
  }

  isOnline() {
    return this.networkService.isOnline();
  }

  syncStatus() {
    return this.syncService.status();
  }

  shouldShowBar(): boolean {
    return !this.isOnline() || 
           this.syncStatus().isActive || 
           this.syncStatus().pendingCount > 0 ||
           this.syncStatus().errors.length > 0;
  }

  async forceSync() {
    try {
      await this.syncService.forcSync();
    } catch (error) {
      console.error('Erreur lors de la synchronisation forcée:', error);
    }
  }

  clearCache() {
    if (confirm('Êtes-vous sûr de vouloir vider le cache ? Cela supprimera toutes les données hors-ligne.')) {
      this.offlineStorage.clearCache();
      this.syncService.clearSyncQueue();
      this.updateCacheInfo();
    }
  }

  formatLastSync(date: Date): string {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    
    if (minutes < 1) return 'à l\'instant';
    if (minutes < 60) return `il y a ${minutes}min`;
    
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `il y a ${hours}h`;
    
    return date.toLocaleDateString();
  }

  formatBytes(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  private updateCacheInfo() {
    this.cacheInfo = this.offlineStorage.getCacheInfo();
  }
}
