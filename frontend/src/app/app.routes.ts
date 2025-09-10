import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { GuideListComponent } from './features/guides/guide-list.component';
import { GuideDetailComponent } from './features/guides/guide-detail.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
	{
		path: '',
		redirectTo: 'mes-guides',
		pathMatch: 'full'
	},
	{
		path: 'login',
		component: LoginComponent
	},
	{
		path: 'mes-guides',
		component: GuideListComponent,
		canActivate: [authGuard]
	},
	{
		path: 'guides/:id',
		component: GuideDetailComponent,
		canActivate: [authGuard]
	},
	{
		path: '**',
		redirectTo: 'mes-guides'
	}
];
