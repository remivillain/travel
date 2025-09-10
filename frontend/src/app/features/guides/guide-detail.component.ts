// guide-detail.component.ts
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { GuideService } from '../../core/services/guide.service';
import { Guide } from '../../core/models/guide.model';
import { GuideActivite } from '../../core/models/guide-activite.model';
import { formatEnum } from '../../core/utils/utils-enum-format';

import { ActivityListComponent } from '../activities/activity-list.component';
@Component({
  selector: 'app-guide-detail',
  standalone: true,
  imports: [CommonModule, ActivityListComponent],
  templateUrl: './guide-detail.component.html',
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
      this.error.set('Impossible de charger ce guide');
      console.error('Error loading guide:', err);
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

  goBack() {
    this.router.navigate(['/guides']);
  }
}