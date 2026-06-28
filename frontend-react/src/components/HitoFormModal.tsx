import { FormEvent, useEffect, useMemo, useState } from 'react';
import type { Hito, HitoInput } from '../api/client';
import { hitoFueraDeRango, mensajeRangoProyecto, type RangoProyecto } from '../utils/hitoValidation';

type Props = {
  open: boolean;
  title: string;
  initial?: Partial<Hito>;
  proyecto?: RangoProyecto;
  saving?: boolean;
  onClose: () => void;
  onSubmit: (body: HitoInput) => Promise<void>;
};

const DEFAULT: HitoInput = {
  titulo: '',
  fecha: '',
  completado: false,
};

export default function HitoFormModal({
  open,
  title,
  initial,
  proyecto,
  saving = false,
  onClose,
  onSubmit,
}: Props) {
  const [form, setForm] = useState<HitoInput>(DEFAULT);

  const rangoMensaje = useMemo(() => (proyecto ? mensajeRangoProyecto(proyecto) : null), [proyecto]);
  const fechaInvalida = useMemo(
    () => (proyecto && form.fecha ? hitoFueraDeRango(form.fecha, proyecto) : false),
    [proyecto, form.fecha],
  );

  const minFecha = proyecto?.fechaInicio?.slice(0, 10);
  const maxFecha = proyecto?.fechaFin?.slice(0, 10);

  useEffect(() => {
    if (!open) return;
    setForm({
      titulo: initial?.titulo ?? '',
      fecha: initial?.fecha ?? '',
      completado: initial?.completado ?? false,
    });
  }, [open, initial]);

  if (!open) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (proyecto && hitoFueraDeRango(form.fecha, proyecto)) return;
    await onSubmit({
      titulo: form.titulo.trim(),
      fecha: form.fecha,
      completado: form.completado,
    });
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
            <label htmlFor="hito-titulo">Título</label>
            <input
              id="hito-titulo"
              value={form.titulo}
              onChange={(e) => setForm((f) => ({ ...f, titulo: e.target.value }))}
              required
              maxLength={200}
            />
          </div>
          <div className="form-group">
            <label htmlFor="hito-fecha">Fecha</label>
            <input
              id="hito-fecha"
              type="date"
              value={form.fecha}
              min={minFecha}
              max={maxFecha}
              onChange={(e) => setForm((f) => ({ ...f, fecha: e.target.value }))}
              required
            />
            {rangoMensaje && (
              <small className="muted" style={{ display: 'block', marginTop: '0.35rem' }}>
                {rangoMensaje}
              </small>
            )}
            {fechaInvalida && (
              <small className="alert-warn" style={{ display: 'block', marginTop: '0.35rem', padding: '0.35rem 0.5rem', borderRadius: 6 }}>
                La fecha seleccionada queda fuera del periodo del proyecto.
              </small>
            )}
          </div>
          <div className="form-group">
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={form.completado}
                onChange={(e) => setForm((f) => ({ ...f, completado: e.target.checked }))}
              />
              Completado
            </label>
          </div>
          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button type="submit" className="btn" disabled={saving || !form.titulo.trim() || !form.fecha || fechaInvalida}>
              {saving ? 'Guardando…' : 'Guardar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
