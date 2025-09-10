import { Injectable, signal } from '@angular/core';
import { Observable, fromEvent, merge, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  private _isOnline = signal(true); // Défaut : en ligne
  
  constructor() {
    // Initialiser à false côté serveur, true côté client par défaut
    if (typeof window === 'undefined') {
      this._isOnline.set(false);
    } else {
      this._isOnline.set(typeof navigator !== 'undefined' ? navigator.onLine : true);
    }
    this.initializeNetworkDetection();
  }

  get isOnline() {
    return this._isOnline.asReadonly();
  }

  get isOnline$(): Observable<boolean> {
    if (typeof window === 'undefined') {
      return of(true);
    }

    return merge(
      fromEvent(window, 'online').pipe(map(() => true)),
      fromEvent(window, 'offline').pipe(map(() => false))
    ).pipe(
      startWith(typeof navigator !== 'undefined' ? navigator.onLine : true)
    );
  }

  private initializeNetworkDetection() {
    if (typeof window === 'undefined') {
      // Côté serveur, on considère toujours hors-ligne pour éviter les requêtes
      this._isOnline.set(false);
      return;
    }

    // Log de l'état initial
    console.log(`🌐 NetworkService initialisé - État: ${this._isOnline() ? 'En ligne' : 'Hors-ligne'}`);
    
    // Écouter les événements de connexion
    window.addEventListener('online', () => {
      this._isOnline.set(true);
      console.log('🌐 Connexion rétablie');
    });

    window.addEventListener('offline', () => {
      this._isOnline.set(false);
      console.log('🔌 Connexion perdue - Mode hors-ligne activé');
    });
  }

  /**
   * Test actif de la connectivité réseau
   */
  async checkConnectivity(): Promise<boolean> {
    if (typeof window === 'undefined') return true;
    
    if (!navigator.onLine) {
      return false;
    }

    try {
      // Test avec une requête HEAD vers un endpoint fiable
      const response = await fetch('/api/health', {
        method: 'HEAD',
        mode: 'no-cors',
        cache: 'no-cache'
      });
      return true;
    } catch {
      // Fallback avec une image pixel
      try {
        const response = await fetch('data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7', {
          method: 'HEAD',
          mode: 'no-cors'
        });
        return false; // Si on arrive ici, on n'a pas de vraie connexion internet
      } catch {
        return false;
      }
    }
  }
}
