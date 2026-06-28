import { useEffect, useMemo, useState } from 'react';
import { api, type Asignatura } from '../api/client';
import { etiquetaTutor } from '../utils/tutorAsignatura';

const MAX_MATRICULAS = 10;

type Props = {
  modo?: 'estudiante' | 'profesor';
  universidadId?: number;
  initialSeleccion: number[];
  onGuardado?: (asignaturas: Asignatura[]) => void;
  submitLabel?: string;
};

export default function MatriculacionSelector({
  modo = 'estudiante',
  universidadId,
  initialSeleccion,
  onGuardado,
  submitLabel = 'Guardar matrículas',
}: Props) {
  const [asignaturas, setAsignaturas] = useState<Asignatura[]>([]);
  const [seleccion, setSeleccion] = useState<Set<number>>(new Set(initialSeleccion));
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setSeleccion(new Set(initialSeleccion));
  }, [initialSeleccion]);

  useEffect(() => {
    if (!universidadId) return;
    (async () => {
      setLoading(true);
      try {
        setAsignaturas(await api.catalogo.asignaturas(universidadId));
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudieron cargar las asignaturas');
      } finally {
        setLoading(false);
      }
    })();
  }, [universidadId]);

  const seleccionadas = useMemo(() => seleccion.size, [seleccion]);

  function toggle(id: number) {
    setSeleccion((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else if (next.size < MAX_MATRICULAS) {
        next.add(id);
      }
      return next;
    });
  }

  async function guardar() {
    if (seleccion.size === 0) {
      setError('Selecciona al menos una asignatura');
      return;
    }
    setBusy(true);
    setError(null);
    try {
      const guardadas = await api.usuarios.updateMisAsignaturas([...seleccion]);
      onGuardado?.(guardadas);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar');
    } finally {
      setBusy(false);
    }
  }

  const esProfesor = modo === 'profesor';

  if (!universidadId) {
    return (
      <p className="muted">
        {esProfesor
          ? 'Indica tu universidad antes de seleccionar asignaturas.'
          : 'Indica tu universidad antes de matricularte.'}
      </p>
    );
  }

  if (loading) return <p>Cargando asignaturas…</p>;

  return (
    <>
      <p className="muted" style={{ marginTop: 0 }}>
        {esProfesor
          ? `Elige las asignaturas que impartes (máximo ${MAX_MATRICULAS}). Las ya asignadas a otro profesor no se muestran.`
          : `Elige las asignaturas en las que estás matriculado (máximo ${MAX_MATRICULAS}).`}
        {' '}
        <strong>{seleccionadas}</strong> seleccionada(s).
      </p>

      {error && <div className="alert alert-warn">{error}</div>}

      {esProfesor && asignaturas.length === 0 && (
        <p className="muted">No hay asignaturas disponibles en tu universidad.</p>
      )}

      <div className="matriculacion-list">
        {asignaturas.map((a) => {
          const marcada = seleccion.has(a.id);
          const bloqueada = !marcada && seleccionadas >= MAX_MATRICULAS;
          return (
            <label
              key={a.id}
              className={`matriculacion-item${marcada ? ' matriculacion-item--selected' : ''}${bloqueada ? ' matriculacion-item--disabled' : ''}`}
            >
              <input
                type="checkbox"
                checked={marcada}
                disabled={bloqueada}
                onChange={() => toggle(a.id)}
              />
              <span>
                <strong>{a.nombre}</strong>
                {a.descripcion && (
                  <small className="muted" style={{ display: 'block' }}>{a.descripcion}</small>
                )}
                {!esProfesor && (
                  <small className="muted">Tutor: {etiquetaTutor(a.tutorNombre)}</small>
                )}
              </span>
            </label>
          );
        })}
      </div>

      <button type="button" className="btn" disabled={busy || seleccionadas === 0} onClick={guardar}>
        {busy ? 'Guardando…' : submitLabel}
      </button>
    </>
  );
}
