const ETIQUETAS: Record<string, string> = {
  PENDIENTE: 'Pendiente',
  EN_PROGRESO: 'En progreso',
  REVISION: 'Revisión',
  HECHA: 'Hecha',
};

const PRIORIDADES: Record<string, string> = {
  BAJA: 'Baja',
  MEDIA: 'Media',
  ALTA: 'Alta',
  URGENTE: 'Urgente',
};

function normalizar(valor: string): string {
  return valor.trim().toUpperCase().replace(/-/g, '_');
}

export function etiquetaEstadoTarea(estado: string): string {
  const key = normalizar(estado);
  if (ETIQUETAS[key]) return ETIQUETAS[key];
  return key
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

export function etiquetaPrioridadTarea(prioridad: string): string {
  const key = normalizar(prioridad);
  if (PRIORIDADES[key]) return PRIORIDADES[key];
  return key
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

/** Color de barra en el diagrama de Gantt según estado de la tarea */
export function colorGanttTarea(estado: string): string {
  const key = normalizar(estado);
  const colores: Record<string, string> = {
    PENDIENTE: '#94a3b8',
    EN_PROGRESO: '#3b82f6',
    REVISION: '#f59e0b',
    HECHA: '#22c55e',
  };
  return colores[key] ?? '#6366f1';
}

export const ESTADOS_TAREA = Object.keys(ETIQUETAS);
export const PRIORIDADES_TAREA = Object.keys(PRIORIDADES);
