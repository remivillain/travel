import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activity-icon',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activity-icon.component.html',
  styleUrls: []
})
export class ActivityIconComponent {
  @Input() category: string = '';
}
