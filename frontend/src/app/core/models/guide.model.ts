import { GuideActivite } from './guide-activite.model';

export interface Guide {
  id: number;
  titre: string;
  description: string;
  nombreJours: number;
  mobilites: string[];
  saisons: string[];
  pourQui: string[];
  guideActivites: GuideActivite[];
  invitedUserIds: number[];
}

