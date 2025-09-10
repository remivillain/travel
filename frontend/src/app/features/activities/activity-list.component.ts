import { Component, Input } from '@angular/core';
import { ActivityDetailComponent } from './activity-detail.component';
import { CommonModule } from '@angular/common';
@Component({
	selector: 'app-activity-list',
	standalone: true,
	imports: [ActivityDetailComponent,CommonModule],
	templateUrl: './activity-list.component.html',
	styleUrls: []
})
export class ActivityListComponent {
	@Input() activities: any[] = [];
}
