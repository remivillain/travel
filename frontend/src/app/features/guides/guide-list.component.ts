// guide-list.component.ts
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GuideService } from '../../core/services/guide.service';
import { GuideFilters } from '../../core/models/guide-filter.model';
import { Guide } from '../../core/models/guide.model';
import { formatEnum } from '../../core/utils/utils-enum-format';
import { EnumService } from '../../core/services/enum.service';

@Component({
  selector: 'app-guide-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './guide-list.component.html',
  styleUrls: ['./guide-list.component.css']
  
})
export class GuideListComponent implements OnInit {
  formatEnum = formatEnum;
  private guideService = inject(GuideService);
  private enumService = inject(EnumService);
  public authService = inject(AuthService);
  private router = inject(Router);

  guides = signal<Guide[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  searchTerm = '';
  filters: GuideFilters = {
    mobility: [],
    season: [],
    audience: []
  };

  mobiliteOptions: string[] = [];
  saisonOptions: string[] = [];
  pourQuiOptions: string[] = [];

  filteredGuides = computed(() => {
    let filtered = this.guides();

    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(guide => 
        guide.titre.toLowerCase().includes(term) ||
        guide.description.toLowerCase().includes(term)
      );
    }

    // Filters
    if (this.filters.mobility && this.filters.mobility.length > 0) {
      filtered = filtered.filter(guide => 
        guide.mobilites?.some(mobility => this.filters.mobility!.includes(mobility))
      );
    }

    if (this.filters.season && this.filters.season.length > 0) {
      filtered = filtered.filter(guide => 
        guide.saisons?.some(season => this.filters.season!.includes(season))
      );
    }

    if (this.filters.audience && this.filters.audience.length > 0) {
      filtered = filtered.filter(guide => 
        guide.pourQui?.some(audience => this.filters.audience!.includes(audience))
      );
    }

    return filtered;
  });

  ngOnInit() {
    this.loadGuides();
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

    try {
      const guides = await this.guideService.getUserGuides();
      this.guides.set(guides);
    } catch (err: any) {
      this.error.set('Impossible de charger les guides');
      console.error('Error loading guides:', err);
    } finally {
      this.loading.set(false);
    }
  }

  applyFilters() {
    // Triggers computed signal update - filteredGuides will automatically recalculate
  }

  toggleFilter(filterType: keyof GuideFilters, value: string) {
    const filter = this.filters[filterType];
    if (!filter) return;

    const index = filter.indexOf(value);
    if (index === -1) {
      filter.push(value);
    } else {
      filter.splice(index, 1);
    }
    // Filtrage automatique via computed signal
  }

  isFilterSelected(filterType: keyof GuideFilters, value: string): boolean {
    const filter = this.filters[filterType];
    return filter ? filter.includes(value) : false;
  }

  getSelectedCount(filterType: keyof GuideFilters): number {
    const filter = this.filters[filterType];
    return filter ? filter.length : 0;
  }

  getFilterButtonClass(isSelected: boolean): string {
    return isSelected 
      ? 'btn-primary px-3 py-1 rounded-full text-xs font-medium transition-colors' 
      : 'px-3 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors';
  }

  hasActiveFilters(): boolean {
    return this.getSelectedCount('mobility') > 0 || 
           this.getSelectedCount('season') > 0 || 
           this.getSelectedCount('audience') > 0;
  }

  clearAllFilters() {
    this.filters.mobility = [];
    this.filters.season = [];
    this.filters.audience = [];
  }

  onSearchChange(event: any) {
    this.searchTerm = event.target.value;
    // Le filtrage se fait automatiquement via computed signal
  }

  openGuide(guide: Guide) {
    this.router.navigate(['/guides', guide.id]);
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
}