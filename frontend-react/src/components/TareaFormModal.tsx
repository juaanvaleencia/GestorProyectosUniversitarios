import { FormEvent, useEffect, useState } from 'react';
import type { Miembro, Tarea, TareaInput } from '../api/client';
import { ESTADOS_TAREA, PRIORIDADES_TAREA, etiquetaEstadoTarea, etiquetaPrioridadTarea } from '../utils/estadoTarea';
import { miembrosAsignables } from '../utils/rolProyecto';

type Props = {
  open: boolean;
  title: string;
  initial?: Partial<Tarea>;
  miembros: Miembro[];
  saving?: boolean;
  modo?: 'normal' | 'subtarea';
  tituloBloqueado?: boolean;
  onClose: () => void;
  onSubmit: (body: TareaInput) => Promise<void>;
};

const DEFAULT: TareaInput = {
  titulo: '',
  descripcion: '',
  estado: 'PENDIENTE',
  prioridad: 'MEDIA',
  responsableUid: '',
  fechaLimite: '',
  orden: 0,
};

export default function TareaFormModal({
  open,
  title,
  initial,
  miembros,
  saving = false,
  modo = 'normal',
  tituloBloqueado = false,
  onClose,
  onSubmit,
}: Props) {
  const asignables = miembrosAsignables(miembros);
  const [form, setForm] = useState<TareaInput>(DEFAULT);

  useEffect(() => {
    if (!open) return;
    setForm({
      titulo: initial?.titulo ?? '',
      descripcion: initial?.descripcion ?? '',
      estado: initial?.estado ?? 'PENDIENTE',
      prioridad: initial?.prioridad ?? 'MEDIA',
      responsableUid: initial?.responsableUid ?? '',
      fechaLimite: initial?.fechaLimite ?? '',
      orden: initial?.orden ?? 0,
    });
  }, [open, initial]);

  if (!open) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const body: TareaInput = {
      ...form,
      titulo: form.titulo.trim(),
      descripcion: form.descripcion?.trim() || undefined,
      responsableUid: form.responsableUid || undefined,
      fechaLimite: form.fechaLimite || undefined,
    };
    await onSubmit(body);
  }

  return (
    <div className="modal-backdrop" onClick={onClose} role="presentation">
      <div className="modal card" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
        <div className="modal-header">
          <h3 style={{ margin: 0 }}>{title}</h3>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Cerrar">
            ×
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="tarea-titulo">Título</label>
            <input
              id="tarea-titulo"
              value={form.titulo}
              onChange={(e) => setForm((f) => ({ ...f, titulo: e.target.value }))}
              required
              maxLength={200}
              disabled={tituloBloqueado}
            />
          </div>
          <div className="form-group">
            <label htmlFor="tarea-descripcion">Descripción</label>
            <textarea
              id="tarea-descripcion"
              rows={3}
              value={form.descripcion ?? ''}
              onChange={(e) => setForm((f) => ({ ...f, descripcion: e.target.value }))}
              maxLength={1000}
            />
          </div>
          {modo === 'normal' && (
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="tarea-estado">Estado</label>
              <select
                id="tarea-estado"
                value={form.estado}
                onChange={(e) => setForm((f) => ({ ...f, estado: e.target.value }))}
              >
                {ESTADOS_TAREA.map((estado) => (
                  <option key={estado} value={estado}>
                    {etiquetaEstadoTarea(estado)}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="tarea-prioridad">Prioridad</label>
              <select
                id="tarea-prioridad"
                value={form.prioridad}
                onChange={(e) => setForm((f) => ({ ...f, prioridad: e.target.value }))}
              >
                {PRIORIDADES_TAREA.map((prioridad) => (
                  <option key={prioridad} value={prioridad}>
                    {etiquetaPrioridadTarea(prioridad)}
                  </option>
                ))}
              </select>
            </div>
          </div>
          )}
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="tarea-responsable">Responsable</label>
              <select
                id="tarea-responsable"
                value={form.responsableUid ?? ''}
                onChange={(e) => setForm((f) => ({ ...f, responsableUid: e.target.value }))}
              >
                <option value="">Sin asignar</option>
                {asignables.map((m) => (
                  <option key={m.usuarioUid} value={m.usuarioUid}>
                    {m.nombre}
                  </option>
                ))}
              </select>
            </div>
            {modo === 'normal' && (
            <div className="form-group">
              <label htmlFor="tarea-fecha">Fecha límite</label>
              <input
                id="tarea-fecha"
                type="date"
                value={form.fechaLimite ?? ''}
                onChange={(e) => setForm((f) => ({ ...f, fechaLimite: e.target.value }))}
              />
            </div>
            )}
          </div>
          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button type="submit" className="btn" disabled={saving || !form.titulo.trim()}>
              {saving ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
