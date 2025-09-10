// utils-enum-format.ts
// Utilitaire pour formater les enums backend en labels lisibles

export function formatEnum(value: string): string {
  const accentMap: { [key: string]: string } = {
    'A_PIED': 'À pied',
    'ETE': 'Été',
    'EN_GROUPE': 'En groupe',
    'ENTRE_AMIS': 'Entre amis',
    'SEUL': 'Seul',
    'FAMILLE': 'Famille',
    'VOITURE': 'Voiture',
    'VELO': 'Vélo',
    'MOTO': 'Moto',
    'PRINTEMPS': 'Printemps',
    'AUTOMNE': 'Automne',
    'HIVER': 'Hiver',
    // Ajoute d'autres cas spécifiques si besoin
  };
  if (accentMap[value]) return accentMap[value];

  // Transformation générique
  let formatted = value
    .toLowerCase()
    .replace(/_/g, ' ');
  formatted = formatted.charAt(0).toUpperCase() + formatted.slice(1);
  return formatted;
}
