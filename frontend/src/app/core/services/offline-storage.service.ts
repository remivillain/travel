import { Injectable } from '@angular/core';
import { Guide } from '../models/guide.model';

export interface CacheEntry<T> {
  data: T;
  timestamp: number;
  expiresAt: number;
}

export interface PendingSync {
  id: string;
  type: 'CREATE' | 'UPDATE' | 'DELETE';
  endpoint: string;
  data?: any;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class OfflineStorageService {
  private readonly CACHE_PREFIX = 'travel_app_';
  private readonly PENDING_SYNC_KEY = 'travel_app_pending_sync';

  constructor() {}

  // === CACHE MANAGEMENT ===

  /**
   * Stocke des données dans le cache local avec TTL
   */
  setCache<T>(key: string, data: T, ttlMinutes: number = 60): void {
    if (typeof window === 'undefined') return;

    const entry: CacheEntry<T> = {
      data,
      timestamp: Date.now(),
      expiresAt: Date.now() + (ttlMinutes * 60 * 1000)
    };

    try {
      localStorage.setItem(this.CACHE_PREFIX + key, JSON.stringify(entry));
    } catch (error) {
      console.warn('Erreur lors de la sauvegarde en cache:', error);
    }
  }

  /**
   * Récupère des données du cache local
   */
  getCache<T>(key: string): T | null {
    if (typeof window === 'undefined') return null;

    try {
      const item = localStorage.getItem(this.CACHE_PREFIX + key);
      if (!item) return null;

      const entry: CacheEntry<T> = JSON.parse(item);
      
      // Vérifier l'expiration
      if (Date.now() > entry.expiresAt) {
        this.removeCache(key);
        return null;
      }

      return entry.data;
    } catch (error) {
      console.warn('Erreur lors de la lecture du cache:', error);
      this.removeCache(key);
      return null;
    }
  }

  /**
   * Supprime une entrée du cache
   */
  removeCache(key: string): void {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(this.CACHE_PREFIX + key);
  }

  /**
   * Vide tout le cache
   */
  clearCache(): void {
    if (typeof window === 'undefined') return;

    const keys = Object.keys(localStorage).filter(key => 
      key.startsWith(this.CACHE_PREFIX)
    );
    
    keys.forEach(key => localStorage.removeItem(key));
  }

  // === GUIDES SPECIFIC CACHE ===

  /**
   * Cache les guides de l'utilisateur
   */
  cacheUserGuides(guides: Guide[]): void {
    this.setCache('user_guides', guides, 720); // 12 heures
    console.log(`📦 ${guides.length} guides mis en cache`);
  }

  /**
   * Récupère les guides depuis le cache
   */
  getCachedUserGuides(): Guide[] | null {
    return this.getCache<Guide[]>('user_guides');
  }

  /**
   * Cache un guide spécifique
   */
  cacheGuide(guide: Guide): void {
    this.setCache(`guide_${guide.id}`, guide, 720); // 12 heures
  }

  /**
   * Récupère un guide spécifique depuis le cache
   */
  getCachedGuide(id: number): Guide | null {
    return this.getCache<Guide>(`guide_${id}`);
  }

  // === SYNC QUEUE MANAGEMENT ===

  /**
   * Ajoute une action à la queue de synchronisation
   */
  addToSyncQueue(action: Omit<PendingSync, 'id' | 'timestamp'>): void {
    if (typeof window === 'undefined') return;

    const pendingSync: PendingSync = {
      ...action,
      id: this.generateId(),
      timestamp: Date.now()
    };

    const queue = this.getSyncQueue();
    queue.push(pendingSync);
    this.saveSyncQueue(queue);
    
    console.log('📝 Action ajoutée à la queue de sync:', pendingSync);
  }

  /**
   * Récupère la queue de synchronisation
   */
  getSyncQueue(): PendingSync[] {
    if (typeof window === 'undefined') return [];

    try {
      const queue = localStorage.getItem(this.PENDING_SYNC_KEY);
      return queue ? JSON.parse(queue) : [];
    } catch {
      return [];
    }
  }

  /**
   * Supprime un élément de la queue de synchronisation
   */
  removeFromSyncQueue(id: string): void {
    if (typeof window === 'undefined') return;

    const queue = this.getSyncQueue().filter(item => item.id !== id);
    this.saveSyncQueue(queue);
  }

  /**
   * Vide la queue de synchronisation
   */
  clearSyncQueue(): void {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(this.PENDING_SYNC_KEY);
  }

  /**
   * Sauvegarde la queue de synchronisation
   */
  private saveSyncQueue(queue: PendingSync[]): void {
    if (typeof window === 'undefined') return;
    localStorage.setItem(this.PENDING_SYNC_KEY, JSON.stringify(queue));
  }

  /**
   * Génère un ID unique
   */
  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  // === CACHE INFO ===

  /**
   * Obtient des informations sur le cache
   */
  getCacheInfo(): { totalSize: number; itemCount: number; items: string[] } {
    if (typeof window === 'undefined') {
      return { totalSize: 0, itemCount: 0, items: [] };
    }

    const items = Object.keys(localStorage)
      .filter(key => key.startsWith(this.CACHE_PREFIX));
    
    let totalSize = 0;
    items.forEach(key => {
      const value = localStorage.getItem(key);
      if (value) {
        totalSize += new Blob([value]).size;
      }
    });

    return {
      totalSize,
      itemCount: items.length,
      items: items.map(key => key.replace(this.CACHE_PREFIX, ''))
    };
  }
}
