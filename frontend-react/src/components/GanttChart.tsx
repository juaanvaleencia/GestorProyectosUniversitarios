import type { Hito, Proyecto, Tarea } from '../api/client';
import { colorGanttTarea, etiquetaEstadoTarea } from '../utils/estadoTarea';

type Props = {
  proyecto: Proyecto;
  hitos: Hito[];
  tareas: Tarea[];
};

function parseDate(s?: string): Date | null {
  if (!s) return null;
  const d = new Date(s);
  return Number.isNaN(d.getTime()) ? null : d;
}

export default function GanttChart({ proyecto, hitos, tareas }: Props) {
  const start = parseDate(proyecto.fechaInicio) ?? new Date();
  const end = parseDate(proyecto.fechaFin) ?? new Date(start.getTime() + 1000 * 60 * 60 * 24 * 90);
  const totalMs = Math.max(end.getTime() - start.getTime(), 1);

  const items: { label: string; from: Date; to: Date; tone: string; tipo?: string }[] = [];

  if (proyecto.fechaInicio && proyecto.fechaFin) {
    items.push({
      label: proyecto.titulo,
      from: start,
      to: end,
      tone: 'var(--accent)',
    });
  }

  hitos.forEach((h) => {
    const d = parseDate(h.fecha);
    if (!d) return;
    const endH = new Date(d.getTime() + 1000 * 60 * 60 * 24 * 3);
    items.push({ label: `Hito: ${h.titulo}`, from: d, to: endH, tone: h.completado ? '#22c55e' : '#eab308' });
  });

  tareas.forEach((t) => {
    const d = parseDate(t.fechaLimite);
    if (!d) return;
    const from = new Date(d.getTime() - 1000 * 60 * 60 * 24 * 7);
    const esSubtarea = Boolean(t.tareaPadreId);
    const esProfesor = t.origen === 'PROFESOR' && !esSubtarea;
    const etiqueta = esSubtarea && t.letraSubtarea
      ? `${t.letraSubtarea}. ${t.titulo}`
      : t.titulo;
    items.push({
      label: etiqueta,
      from,
      to: d,
      tone: colorGanttTarea(t.estado),
      tipo: esSubtarea
        ? 'gantt-subtarea'
        : esProfesor
          ? 'gantt-profesor'
          : `tarea-${t.estado.toLowerCase().replace(/_/g, '-')}`,
    });
  });

  const hayTareas = tareas.some((t) => parseDate(t.fechaLimite));

  function leftPct(d: Date) {
    return `${Math.max(0, Math.min(100, ((d.getTime() - start.getTime()) / totalMs) * 100))}%`;
  }

  function widthPct(from: Date, to: Date) {
    return `${Math.max(2, Math.min(100, ((to.getTime() - from.getTime()) / totalMs) * 100))}%`;
  }

  return (
    <div className="gantt">
      {hayTareas && (
        <div className="gantt-legend">
          {(['PENDIENTE', 'EN_PROGRESO', 'REVISION', 'HECHA'] as const).map((estado) => (
            <span key={estado} className="gantt-legend-item">
              <span
                className="gantt-legend-dot"
                style={{ background: colorGanttTarea(estado) }}
              />
              {etiquetaEstadoTarea(estado)}
            </span>
          ))}
        </div>
      )}
      <div className="gantt-axis">
        <span>{start.toLocaleDateString('es-ES')}</span>
        <span>{end.toLocaleDateString('es-ES')}</span>
      </div>
      {items.length === 0 ? (
        <p className="muted">Añade fechas al proyecto, hitos o tareas para ver el cronograma.</p>
      ) : (
        items.map((item, i) => (
          <div key={i} className="gantt-row">
            <div className="gantt-label">{item.label}</div>
            <div className="gantt-track">
              <div
                className={`gantt-bar${item.tipo ? ` ${item.tipo}` : ''}`}
                style={{
                  marginLeft: leftPct(item.from),
                  width: widthPct(item.from, item.to),
                  background: item.tone,
                }}
                title={`${item.from.toLocaleDateString('es-ES')} – ${item.to.toLocaleDateString('es-ES')}`}
              />
            </div>
          </div>
        ))
      )}
    </div>
  );
}
