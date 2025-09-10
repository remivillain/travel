import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col justify-center items-center py-12">
      <div class="spinner-container">
        <!-- Spinner principal -->
        <div class="spinner">
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
          
          <!-- Icône centrale -->
          <div class="spinner-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2L13.09 8.26L22 9L13.09 9.74L12 16L10.91 9.74L2 9L10.91 8.26L12 2Z" opacity="0.8"/>
              <circle cx="12" cy="12" r="2" opacity="0.6"/>
            </svg>
          </div>
        </div>
        
        <!-- Points décoratifs -->
        <div class="spinner-dots">
          <div class="dot dot-1"></div>
          <div class="dot dot-2"></div>
          <div class="dot dot-3"></div>
          <div class="dot dot-4"></div>
        </div>
      </div>
      <p class="text-gray-600 font-medium mt-6">{{ message || 'Chargement en cours...' }}</p>
    </div>
  `,
  styles: [`
    .spinner-container {
      position: relative;
      width: 120px;
      height: 120px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .spinner {
      position: relative;
      width: 80px;
      height: 80px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    /* Anneaux du spinner */
    .spinner-ring {
      position: absolute;
      border-radius: 50%;
      border: 3px solid transparent;
    }

    .spinner-ring:nth-child(1) {
      width: 80px;
      height: 80px;
      border-top: 3px solid #3b82f6;
      border-right: 3px solid #3b82f6;
      animation: spin 1.5s linear infinite;
    }

    .spinner-ring:nth-child(2) {
      width: 60px;
      height: 60px;
      border-bottom: 3px solid #8b5cf6;
      border-left: 3px solid #8b5cf6;
      animation: spin 1.2s linear infinite reverse;
    }

    .spinner-ring:nth-child(3) {
      width: 40px;
      height: 40px;
      border-top: 3px solid #06b6d4;
      border-right: 3px solid #06b6d4;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    /* Icône centrale */
    .spinner-icon {
      position: absolute;
      z-index: 10;
      color: #6b7280;
      animation: icon-pulse 2s ease-in-out infinite;
    }

    @keyframes icon-pulse {
      0%, 100% { 
        transform: scale(1);
        opacity: 0.7;
      }
      50% { 
        transform: scale(1.1);
        opacity: 1;
      }
    }

    /* Points décoratifs autour du spinner */
    .spinner-dots {
      position: absolute;
      width: 100%;
      height: 100%;
      top: 0;
      left: 0;
    }

    .dot {
      position: absolute;
      width: 6px;
      height: 6px;
      background: linear-gradient(45deg, #3b82f6, #8b5cf6);
      border-radius: 50%;
      animation: dot-orbit 3s linear infinite;
    }

    .dot-1 {
      top: 10px;
      left: 50%;
      transform: translateX(-50%);
      animation-delay: 0s;
    }

    .dot-2 {
      top: 50%;
      right: 10px;
      transform: translateY(-50%);
      animation-delay: 0.75s;
    }

    .dot-3 {
      bottom: 10px;
      left: 50%;
      transform: translateX(-50%);
      animation-delay: 1.5s;
    }

    .dot-4 {
      top: 50%;
      left: 10px;
      transform: translateY(-50%);
      animation-delay: 2.25s;
    }

    @keyframes dot-orbit {
      0%, 100% {
        opacity: 0.3;
        transform: scale(0.8);
      }
      25% {
        opacity: 1;
        transform: scale(1.2);
      }
      50% {
        opacity: 0.6;
        transform: scale(1);
      }
      75% {
        opacity: 0.8;
        transform: scale(1.1);
      }
    }

    /* Hover effect - Accélération */
    .spinner-container:hover .spinner-ring:nth-child(1) {
      animation-duration: 1s;
      border-color: #1d4ed8 transparent transparent transparent;
    }

    .spinner-container:hover .spinner-ring:nth-child(2) {
      animation-duration: 0.8s;
      border-color: transparent transparent #7c3aed transparent;
    }

    .spinner-container:hover .spinner-ring:nth-child(3) {
      animation-duration: 0.5s;
      border-color: #0891b2 transparent transparent transparent;
    }

    .spinner-container:hover .spinner-icon {
      animation-duration: 1s;
      color: #3b82f6;
    }

    .spinner-container:hover .dot {
      animation-duration: 2s;
    }

    /* Animation de pulsation générale */
    .spinner-container {
      animation: container-pulse 3s ease-in-out infinite;
    }

    @keyframes container-pulse {
      0%, 100% {
        transform: scale(1);
        filter: drop-shadow(0 4px 12px rgba(59, 130, 246, 0.15));
      }
      50% {
        transform: scale(1.02);
        filter: drop-shadow(0 6px 16px rgba(59, 130, 246, 0.25));
      }
    }
  `]
})
export class LoadingSpinnerComponent {
  @Input() message?: string;
}
