import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { NetworkService } from '../../core/services/network.service';
import { Router } from '@angular/router';

@Component({
	selector: 'app-header',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './header.component.html',
	styleUrls: []
})
export class HeaderComponent {
	@Input() userEmail?: string;

	constructor(
		private authService: AuthService,
		public networkService: NetworkService,
		private router: Router
	) {}

	isLoginPage(): boolean {
		return this.router.url === '/login';
	}

	onLogout() {
		this.authService.logout();
		window.location.href = '/login';
	}
}
