import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col justify-center items-center py-12">
      <div class="bike-loader">
        <!-- Bonhomme -->
        <div class="bike-body">
          <svg class="w-10 h-8 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
            <!-- Tête -->
            <circle cx="12" cy="6" r="2"/>
            <!-- Corps -->
            <path d="M10 10h4l-1 8h-2l-1-8z"/>
            <!-- Bras -->
            <path d="M10 12l-2 2M14 12l2 2"/>
            <!-- Jambes pédalant -->
            <path d="M11 16l-1 3M13 16l1 3"/>
          </svg>
        </div>
        
        <!-- Cadre du vélo -->
        <div class="bike-frame"></div>
        
        <!-- Roues -->
        <div class="bike-wheels">
          <div class="wheel wheel-back">
            <div class="w-1 h-1 bg-blue-600 rounded-full absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"></div>
          </div>
          <div class="wheel wheel-front">
            <div class="w-1 h-1 bg-blue-600 rounded-full absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"></div>
          </div>
        </div>
      </div>
      <p class="text-blue-600 font-medium mt-4">{{ message || 'Chargement en cours...' }}</p>
    </div>
  `,
  styleUrls: []
})
export class LoadingSpinnerComponent {
  @Input() message?: string;
}
