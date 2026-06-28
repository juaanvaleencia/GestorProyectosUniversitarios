const ETIQUETAS: Record<string, string> = {
  PLANIFICACION: 'Planificación',
  EN_CURSO: 'En curso',
  PAUSADO: 'Pausado',
  FINALIZADO: 'Finalizado',
};

const CLASES_BADGE: Record<string, string> = {
  PLANIFICACION: 'planificacion',
  EN_CURSO: 'en-curso',
  PAUSADO: 'pausado',
  FINALIZADO: 'finalizado',
};

function normalizarEstado(estado: string): string {
  return estado.trim().toUpperCase().replace(/-/g, '_');
}

export function etiquetaEstadoProyecto(estado: string): string {
  const key = normalizarEstado(estado);
  if (ETIQUETAS[key]) return ETIQUETAS[key];
  return key
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

export function claseBadgeEstadoProyecto(estado: string): string {
  const key = normalizarEstado(estado);
  return CLASES_BADGE[key] ?? 'default';
}

export const ESTADOS_PROYECTO = Object.keys(ETIQUETAS);
