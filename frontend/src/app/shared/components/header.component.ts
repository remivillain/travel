import { Component, Input } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: []
})
export class HeaderComponent {
	@Input() userEmail?: string;

	constructor(private authService: AuthService) {}

	onLogout() {
		this.authService.logout();
		window.location.href = '/login';
	}
}
