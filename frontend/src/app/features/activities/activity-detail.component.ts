import { CommonModule, TitleCasePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActivityIconComponent } from './icons/activity-icon.component';

@Component({
  selector: 'app-activity-detail',
  standalone: true,
  imports: [TitleCasePipe, CommonModule, ActivityIconComponent],
  templateUrl: './activity-detail.component.html',
  styleUrls: []
})
export class ActivityDetailComponent {
  @Input() activity: any;
}
