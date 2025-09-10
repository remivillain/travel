import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkService } from '../../core/services/network.service';
import { SyncService } from '../../core/services/sync.service';
import { OfflineStorageService } from '../../core/services/offline-storage.service';

@Component({
  selector: 'app-offline-demo',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="max-w-4xl mx-auto p-6 space-y-6">
      <div class="card p-6">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">🔌 Démonstration Mode Hors-ligne</h2>
        
        <!-- Status réseau -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Statut de connexion</h3>
          <div class="flex items-center space-x-2">
            <div class="w-3 h-3 rounded-full" 
                 [class.bg-green-500]="networkService.isOnline()" 
                 [class.bg-red-500]="!networkService.isOnline()">
            </div>
            <span class="font-medium">
              {{ networkService.isOnline() ? 'En ligne' : 'Hors-ligne' }}
            </span>
          </div>
        </div>

        <!-- Informations de synchronisation -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Synchronisation</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-blue-50 p-3 rounded-lg">
              <div class="text-sm text-blue-600 font-medium">Actions en attente</div>
              <div class="text-2xl font-bold text-blue-800">{{ syncService.status().pendingCount }}</div>
            </div>
            <div class="bg-green-50 p-3 rounded-lg">
              <div class="text-sm text-green-600 font-medium">Dernière sync</div>
              <div class="text-sm text-green-800">
                {{ syncService.status().lastSync ? formatDate(syncService.status().lastSync!) : 'Jamais' }}
              </div>
            </div>
            <div class="bg-orange-50 p-3 rounded-lg">
              <div class="text-sm text-orange-600 font-medium">Status</div>
              <div class="text-sm text-orange-800">
                {{ syncService.status().isActive ? 'Synchronisation...' : 'Inactif' }}
              </div>
            </div>
          </div>
        </div>

        <!-- Informations du cache -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Cache local</h3>
          <div class="bg-gray-50 p-4 rounded-lg">
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span class="font-medium">Éléments en cache:</span>
                <span class="ml-2">{{ cacheInfo().itemCount }}</span>
              </div>
              <div>
                <span class="font-medium">Taille totale:</span>
                <span class="ml-2">{{ formatBytes(cacheInfo().totalSize) }}</span>
              </div>
            </div>
            <div class="mt-2">
              <span class="font-medium text-sm">Éléments:</span>
              <div class="mt-1 flex flex-wrap gap-1">
                <span *ngFor="let item of cacheInfo().items.slice(0, 5)" 
                      class="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                  {{ item }}
                </span>
                <span *ngIf="cacheInfo().items.length > 5" 
                      class="inline-block bg-gray-200 text-gray-600 text-xs px-2 py-1 rounded">
                  +{{ cacheInfo().items.length - 5 }} autres...
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions de test -->
        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Actions de test</h3>
          <div class="space-y-2">
            <button 
              (click)="testOfflineAction()"
              class="btn-primary mr-2"
              [disabled]="syncService.status().isActive">
              Simuler action hors-ligne
            </button>
            <button 
              (click)="forcSync()"
              class="btn-link-style mr-2"
              [disabled]="!networkService.isOnline() || syncService.status().isActive">
              Forcer synchronisation
            </button>
            <button 
              (click)="clearCache()"
              class="btn-link-style text-red-600 hover:text-red-800">
              Vider le cache
            </button>
          </div>
        </div>

        <!-- Log des actions -->
        <div *ngIf="actionLog().length > 0">
          <h3 class="text-lg font-semibold mb-2">Journal des actions</h3>
          <div class="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm max-h-40 overflow-y-auto">
            <div *ngFor="let log of actionLog()" class="mb-1">
              [{{ formatTime(log.timestamp) }}] {{ log.message }}
            </div>
          </div>
        </div>

        <!-- Instructions -->
        <div class="mt-6 p-4 bg-blue-50 rounded-lg">
          <h4 class="font-semibold text-blue-800 mb-2">💡 Instructions de test</h4>
          <ol class="text-sm text-blue-700 space-y-1 list-decimal list-inside">
            <li>Cliquez sur "Simuler action hors-ligne" pour ajouter des actions à la queue</li>
            <li>Ouvrez les outils de développement (F12) → Network → Cochez "Offline"</li>
            <li>Observez la barre de statut hors-ligne qui apparaît</li>
            <li>Faites des actions dans l'app (navigation, favoris, etc.)</li>
            <li>Décochez "Offline" pour simuler le retour de connexion</li>
            <li>Observez la synchronisation automatique des actions en attente</li>
          </ol>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class OfflineDemoComponent implements OnInit {
  readonly cacheInfo = signal({ itemCount: 0, totalSize: 0, items: [] as string[] });
  readonly actionLog = signal<{ timestamp: Date; message: string }[]>([]);

  constructor(
    public networkService: NetworkService,
    public syncService: SyncService,
    private offlineStorage: OfflineStorageService
  ) {}

  ngOnInit() {
    this.updateCacheInfo();
    
    // Mise à jour périodique
    setInterval(() => {
      this.updateCacheInfo();
    }, 2000);

    // Logger les changements de statut réseau
    this.networkService.isOnline$.subscribe(isOnline => {
      this.addLog(isOnline ? '🌐 Connexion rétablie' : '🔌 Connexion perdue');
    });
  }

  testOfflineAction() {
    // Simuler différents types d'actions
    const actions = [
      { type: 'UPDATE', endpoint: '/guides/1/favorite', data: {} },
      { type: 'CREATE', endpoint: '/guides', data: { title: 'Test Guide' } },
      { type: 'DELETE', endpoint: '/guides/999' }
    ];

    const randomAction = actions[Math.floor(Math.random() * actions.length)];
    
    if (randomAction.type === 'CREATE') {
      this.syncService.queueCreate(randomAction.endpoint, randomAction.data);
    } else if (randomAction.type === 'UPDATE') {
      this.syncService.queueUpdate(randomAction.endpoint, randomAction.data);
    } else {
      this.syncService.queueDelete(randomAction.endpoint);
    }

    this.addLog(`📝 Action ${randomAction.type} ajoutée à la queue: ${randomAction.endpoint}`);
  }

  async forcSync() {
    try {
      this.addLog('🔄 Synchronisation forcée démarrée...');
      await this.syncService.forcSync();
      this.addLog('✅ Synchronisation forcée terminée');
    } catch (error) {
      this.addLog(`❌ Erreur de synchronisation: ${error}`);
    }
  }

  clearCache() {
    if (confirm('Êtes-vous sûr de vouloir vider tout le cache ?')) {
      this.offlineStorage.clearCache();
      this.syncService.clearSyncQueue();
      this.updateCacheInfo();
      this.addLog('🗑️ Cache vidé');
    }
  }

  private updateCacheInfo() {
    this.cacheInfo.set(this.offlineStorage.getCacheInfo());
  }

  private addLog(message: string) {
    this.actionLog.update(logs => [
      ...logs.slice(-9), // Garder seulement les 10 derniers logs
      { timestamp: new Date(), message }
    ]);
  }

  formatBytes(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  formatDate(date: Date): string {
    return date.toLocaleString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString('fr-FR');
  }
}
