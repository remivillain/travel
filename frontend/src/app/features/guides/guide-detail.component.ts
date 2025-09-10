// guide-detail.component.ts
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { GuideService } from '../../core/services/guide.service';
import { Guide } from '../../core/models/guide.model';
import { GuideActivite } from '../../core/models/guide-activite.model';
import { formatEnum } from '../../core/utils/utils-enum-format';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner.component';

import { ActivityListComponent } from '../activities/activity-list.component';
@Component({
  selector: 'app-guide-detail',
  standalone: true,
  imports: [CommonModule, ActivityListComponent, LoadingSpinnerComponent],
  templateUrl: './guide-detail.component.html',
  styleUrls: ['./guide-detail.component.css']
})
export class GuideDetailComponent implements OnInit {
  formatEnum = formatEnum;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private guideService = inject(GuideService);

  guide = signal<Guide | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  selectedDay = signal(0); // 0 = all days, 1+ = specific day
  isFavorite = signal(false);

  ngOnInit() {
    const guideId = this.route.snapshot.params['id'];
    if (guideId) {
      this.loadGuide(guideId);
    }
  }

  async loadGuide(id: number) {
    this.loading.set(true);
    this.error.set(null);

    try {
      const guide = await this.guideService.getGuide(id);
      this.guide.set(guide);
    } catch (err: any) {
      console.error('Error loading guide:', err);
      
      // Gérer les différents types d'erreurs
      if (err.status === 403) {
        this.error.set('Accès refusé : Vous n\'êtes pas invité à consulter ce guide.');
      } else if (err.status === 404) {
        this.error.set('Guide non trouvé.');
      } else if (err.status === 401) {
        this.error.set('Vous devez être connecté pour accéder à ce guide.');
      } else {
        this.error.set('Impossible de charger ce guide. Veuillez réessayer.');
      }
    } finally {
      this.loading.set(false);
    }
  }

  getDaysList(): number[] {
    const guide = this.guide();
    return guide ? Array.from({ length: guide.nombreJours }, (_, i) => i + 1) : [];
  }

  getFilteredActivities(): GuideActivite[] {
    const guide = this.guide();
    if (!guide?.guideActivites) return [];

    let activities = [...guide.guideActivites];

    // Filter by selected day
    if (this.selectedDay() > 0) {
      activities = activities.filter(activity => activity.jour === this.selectedDay());
    }

    // Sort by order
    return activities.sort((a, b) => (a.ordre || 0) - (b.ordre || 0));
  }

  // Trigger animation when day selection changes
  onDaySelect(day: number) {
    // Sauvegarder la position de scroll actuelle
    const scrollPosition = window.scrollY;
    
    // Trigger exit animation
    const container = document.querySelector('.activities-container');
    if (container) {
      container.classList.add('activity-leave');
      
      setTimeout(() => {
        // Changer le jour sélectionné
        this.selectedDay.set(day);
        
        // Maintenir la position de scroll et ajouter l'animation d'entrée
        requestAnimationFrame(() => {
          window.scrollTo({ top: scrollPosition, behavior: 'auto' });
          container.classList.remove('activity-leave');
          container.classList.add('activity-enter');
          
          setTimeout(() => {
            container.classList.remove('activity-enter');
          }, 450); // Correspond à l'animation + délais échelonnés
        });
      }, 250); // Durée réduite pour la sortie
    } else {
      // Fallback sans animation
      this.selectedDay.set(day);
      requestAnimationFrame(() => {
        window.scrollTo({ top: scrollPosition, behavior: 'auto' });
      });
    }
  }

  goBack() {
    this.router.navigate(['/guides']);
  }
}