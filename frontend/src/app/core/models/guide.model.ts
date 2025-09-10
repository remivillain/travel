export interface Guide {
  id: number;
  title: string;
  description: string;
  days: number;
  activities?: any[];
  options: {
    mobility?: string[];
    season?: string[];
    audience?: string[];
  };
}

