import { Activite } from './activite.model';

export interface GuideActivite {
  id: number;
  guideId: number;
  activiteId: number;
  jour: number;
  ordre: number;
  activite: Activite;
}
