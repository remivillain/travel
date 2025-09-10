// guide-list.component.ts
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GuideService } from '../../core/services/guide.service';
import { GuideFilters } from '../../core/models/guide-filter.model';
import { Guide } from '../../core/models/guide.model';

@Component({
  selector: 'app-guide-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './guide-list.component.html',
  styleUrls: ['./guide-list.component.css']
  
})
export class GuideListComponent implements OnInit {
  private guideService = inject(GuideService);
  public authService = inject(AuthService);
  private router = inject(Router);

  guides = signal<Guide[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  
  searchTerm = '';
  filters: GuideFilters = {
    mobility: '',
    season: '',
    audience: ''
  };

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
    if (this.filters.mobility) {
      filtered = filtered.filter(guide => 
        guide.mobilites?.includes(this.filters.mobility!)
      );
    }

    if (this.filters.season) {
      filtered = filtered.filter(guide => 
        guide.saisons?.includes(this.filters.season!)
      );
    }

    if (this.filters.audience) {
      filtered = filtered.filter(guide => 
        guide.pourQui?.includes(this.filters.audience!)
      );
    }

    return filtered;
  });

  ngOnInit() {
    this.loadGuides();
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
    // Triggers computed signal update
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