// guide-list.component.ts
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GuideService } from '../../core/services/guide.service';
import { NetworkService } from '../../core/services/network.service';
import { GuideFilters } from '../../core/models/guide-filter.model';
import { Guide } from '../../core/models/guide.model';
import { formatEnum } from '../../core/utils/utils-enum-format';
import { EnumService } from '../../core/services/enum.service';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner.component';

@Component({
  selector: 'app-guide-list',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './guide-list.component.html',
  styleUrls: ['./guide-list.component.css']
  
})
export class GuideListComponent implements OnInit {
  formatEnum = formatEnum;
  
  private guideService = inject(GuideService);
  private enumService = inject(EnumService);
  public authService = inject(AuthService);
  private router = inject(Router);
  private networkService = inject(NetworkService);

  guides = signal<Guide[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  searchTerm = signal('');
  filters = signal<GuideFilters>({
    mobility: [],
    season: [],
    audience: []
  });
  showGuidesAnimation = signal(false);

  mobiliteOptions: string[] = [];
  saisonOptions: string[] = [];
  pourQuiOptions: string[] = [];

  filteredGuides = computed(() => {
    let filtered = this.guides();
    const currentFilters = this.filters();
    const searchTerm = this.searchTerm();

    // Search filter
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(guide => 
        guide.titre.toLowerCase().includes(term) ||
        guide.description.toLowerCase().includes(term)
      );
    }

    // Filters
    if (currentFilters.mobility && currentFilters.mobility.length > 0) {
      filtered = filtered.filter(guide => 
        guide.mobilites?.some(mobility => currentFilters.mobility!.includes(mobility))
      );
    }

    if (currentFilters.season && currentFilters.season.length > 0) {
      filtered = filtered.filter(guide => 
        guide.saisons?.some(season => currentFilters.season!.includes(season))
      );
    }

    if (currentFilters.audience && currentFilters.audience.length > 0) {
      filtered = filtered.filter(guide => 
        guide.pourQui?.some(audience => currentFilters.audience!.includes(audience))
      );
    }

    return filtered;
  });

  ngOnInit() {
    this.loadGuides();
    this.loadEnums();
    
    // Recharger automatiquement quand la connexion revient
    this.networkService.isOnline$.subscribe(isOnline => {
      if (isOnline && this.guides().length === 0) {
        console.log('🌐 Connexion rétablie - rechargement des guides...');
        this.loadGuides();
        this.loadEnums();
      }
    });
  }

  private loadEnums() {
    this.enumService.getMobilites().subscribe({
      next: opts => this.mobiliteOptions = opts,
      error: () => this.mobiliteOptions = []
    });
    this.enumService.getSaisons().subscribe({
      next: opts => this.saisonOptions = opts,
      error: () => this.saisonOptions = []
    });
    this.enumService.getPourQui().subscribe({
      next: opts => this.pourQuiOptions = opts,
      error: () => this.pourQuiOptions = []
    });
  }

  async loadGuides() {
    this.loading.set(true);
    this.error.set(null);
    this.showGuidesAnimation.set(false);

    try {
      const guides = await this.guideService.getUserGuides();
      this.guides.set(guides);
      
      // Précharger les détails de tous les guides pour le mode hors-ligne
      // (en arrière-plan, sans bloquer l'interface)
      this.preloadGuideDetails(guides);
      
      // Déclencher l'animation après un court délai
      setTimeout(() => {
        this.showGuidesAnimation.set(true);
      }, 100);
    } catch (err: any) {
      // Si on a une erreur mais qu'on est hors-ligne, c'est normal
      if (!this.networkService.isOnline()) {
        this.error.set('Aucune donnée disponible hors-ligne. Connectez-vous pour charger les guides.');
      } else {
        this.error.set('Impossible de charger les guides. Vérifiez votre connexion.');
      }
      console.error('Error loading guides:', err);
    } finally {
      this.loading.set(false);
    }
  }

  private async preloadGuideDetails(guides: Guide[]) {
    // Précharger les détails en arrière-plan pour le mode hors-ligne
    // On fait cela avec un délai pour ne pas surcharger l'API
    for (let i = 0; i < guides.length; i++) {
      setTimeout(async () => {
        try {
          await this.guideService.getGuide(guides[i].id);
          console.log(`📦 Guide ${guides[i].id} préchargé en cache`);
        } catch (error) {
          console.warn(`⚠️ Impossible de précharger le guide ${guides[i].id}:`, error);
        }
      }, i * 200); // 200ms entre chaque préchargement
    }
  }

  applyFilters() {
    // Triggers computed signal update - filteredGuides will automatically recalculate
  }

  toggleFilter(filterType: keyof GuideFilters, value: string) {
    const currentFilters = this.filters();
    const filter = currentFilters[filterType];
    if (!filter) return;

    const index = filter.indexOf(value);
    if (index === -1) {
      filter.push(value);
    } else {
      filter.splice(index, 1);
    }
    
    // Mettre à jour le signal avec le nouvel objet
    this.filters.set({ ...currentFilters });
    
    // Déclencher l'animation pour les nouveaux résultats
    this.triggerGuidesAnimation();
  }

  isFilterSelected(filterType: keyof GuideFilters, value: string): boolean {
    const currentFilters = this.filters();
    const filter = currentFilters[filterType];
    return filter ? filter.includes(value) : false;
  }

  getSelectedCount(filterType: keyof GuideFilters): number {
    const currentFilters = this.filters();
    const filter = currentFilters[filterType];
    return filter ? filter.length : 0;
  }

  getFilterButtonClass(isSelected: boolean): string {
    return isSelected 
      ? 'filter-button btn-primary px-3 py-1 rounded-full text-xs font-medium' 
      : 'filter-button px-3 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-700 hover:bg-blue-50 hover:text-blue-600';
  }

  hasActiveFilters(): boolean {
    return this.getSelectedCount('mobility') > 0 || 
           this.getSelectedCount('season') > 0 || 
           this.getSelectedCount('audience') > 0;
  }

  clearAllFilters() {
    this.filters.set({
      mobility: [],
      season: [],
      audience: []
    });
    
    // Déclencher l'animation pour afficher tous les guides
    this.triggerGuidesAnimation();
  }

  onSearchChange(event: any) {
    this.searchTerm.set(event.target.value);
    // Le filtrage se fait automatiquement via computed signal
    
    // Déclencher l'animation pour les nouveaux résultats
    this.triggerGuidesAnimation();
  }

  openGuide(guide: Guide, event?: Event) {
    // Ajouter l'animation de clic
    if (event) {
      const target = (event.currentTarget as HTMLElement);
      target.classList.add('guide-card-click');
      
      // Délai pour laisser l'animation se jouer avant la navigation
      setTimeout(() => {
        this.router.navigate(['/guides', guide.id]);
      }, 150);
    } else {
      this.router.navigate(['/guides', guide.id]);
    }
  }

  getGuideOptions(guide: Guide): string[] {
    const options: string[] = [];
    if (guide.mobilites) options.push(...guide.mobilites);
    if (guide.saisons) options.push(...guide.saisons);
    if (guide.pourQui) options.push(...guide.pourQui);
    return options.slice(0, 3); // Limit to 3 options for display
  }

  trackByGuide(index: number, guide: Guide): any {
    return guide.id;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Méthode pour déclencher les animations lors des changements de filtres
  private triggerGuidesAnimation() {
    this.showGuidesAnimation.set(false);
    setTimeout(() => {
      this.showGuidesAnimation.set(true);
    }, 50);
  }
}