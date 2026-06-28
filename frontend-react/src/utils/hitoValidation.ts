export type RangoProyecto = {
  fechaInicio?: string;
  fechaFin?: string;
};

function toIsoDate(value?: string): string | null {
  if (!value) return null;
  return value.slice(0, 10);
}

export function hitoFueraDeRango(fecha: string, proyecto: RangoProyecto): boolean {
  const hitoDate = toIsoDate(fecha);
  if (!hitoDate) return false;

  const inicio = toIsoDate(proyecto.fechaInicio);
  const fin = toIsoDate(proyecto.fechaFin);
  if (!inicio && !fin) return false;

  if (inicio && hitoDate < inicio) return true;
  if (fin && hitoDate > fin) return true;
  return false;
}

export function mensajeRangoProyecto(proyecto: RangoProyecto): string | null {
  const inicio = toIsoDate(proyecto.fechaInicio);
  const fin = toIsoDate(proyecto.fechaFin);
  if (!inicio && !fin) return null;
  if (inicio && fin) return `La fecha debe estar entre ${inicio} y ${fin} (fechas del proyecto).`;
  if (inicio) return `La fecha no puede ser anterior a ${inicio} (inicio del proyecto).`;
  return `La fecha no puede ser posterior a ${fin} (fin del proyecto).`;
}

export function progresoHitos(hitos: { completado: boolean }[]): {
  completados: number;
  total: number;
  porcentaje: number;
} {
  const total = hitos.length;
  const completados = hitos.filter((h) => h.completado).length;
  return {
    completados,
    total,
    porcentaje: total === 0 ? 0 : Math.round((completados / total) * 100),
  };
}
