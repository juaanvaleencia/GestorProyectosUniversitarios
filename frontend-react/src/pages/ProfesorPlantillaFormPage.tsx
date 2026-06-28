import { FormEvent, useEffect, useState } from 'react';
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom';
import { api, type PlantillaProyectoInput } from '../api/client';
import { useAuth } from '../auth/AuthContext';

type TareaDraft = { titulo: string; descripcion: string; fechaLimiteSugerida: string };
type HitoDraft = { titulo: string; fechaSugerida: string };

const tareaVacia = (): TareaDraft => ({ titulo: '', descripcion: '', fechaLimiteSugerida: '' });
const hitoVacio = (): HitoDraft => ({ titulo: '', fechaSugerida: '' });

export default function ProfesorPlantillaFormPage() {
  const { asignaturaId, plantillaId } = useParams();
  const id = Number(asignaturaId);
  const plId = plantillaId ? Number(plantillaId) : null;
  const editando = plId != null && !Number.isNaN(plId);
  const navigate = useNavigate();
  const { perfil } = useAuth();

  const asignatura = (perfil?.asignaturasImpartidas ?? []).find((a) => a.id === id);

  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [tareas, setTareas] = useState<TareaDraft[]>([tareaVacia()]);
  const [hitos, setHitos] = useState<HitoDraft[]>([hitoVacio()]);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [cargando, setCargando] = useState(editando);

  useEffect(() => {
    if (!editando || !plId) return;
    (async () => {
      setCargando(true);
      setError(null);
      try {
        const det = await api.catalogo.plantilla(plId);
        setTitulo(det.titulo);
        setDescripcion(det.descripcion ?? '');
        setFechaInicio(det.fechaInicioSugerida?.slice(0, 10) ?? '');
        setFechaFin(det.fechaFinSugerida?.slice(0, 10) ?? '');
        setTareas(
          det.tareas.length > 0
            ? det.tareas.map((t) => ({
                titulo: t.titulo,
                descripcion: t.descripcion ?? '',
                fechaLimiteSugerida: t.fechaLimiteSugerida?.slice(0, 10) ?? '',
              }))
            : [tareaVacia()],
        );
        setHitos(
          det.hitos.length > 0
            ? det.hitos.map((h) => ({
                titulo: h.titulo,
                fechaSugerida: h.fechaSugerida?.slice(0, 10) ?? '',
              }))
            : [hitoVacio()],
        );
      } catch (err) {
        setError(err instanceof Error ? err.message : 'No se pudo cargar la plantilla');
      } finally {
        setCargando(false);
      }
    })();
  }, [editando, plId]);

  if (!perfil) return <p>Cargando…</p>;
  if (!asignatura || Number.isNaN(id)) {
    return <Navigate to="/profesor" replace />;
  }

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!titulo.trim()) {
      setError('Indica un título para la plantilla');
      return;
    }

    const body: PlantillaProyectoInput = {
      titulo: titulo.trim(),
      descripcion: descripcion.trim() || undefined,
      fechaInicioSugerida: fechaInicio || undefined,
      fechaFinSugerida: fechaFin || undefined,
      tareas: tareas
        .filter((t) => t.titulo.trim())
        .map((t, i) => ({
          titulo: t.titulo.trim(),
          descripcion: t.descripcion.trim() || undefined,
          orden: i + 1,
          fechaLimiteSugerida: t.fechaLimiteSugerida || undefined,
        })),
      hitos: hitos
        .filter((h) => h.titulo.trim() && h.fechaSugerida)
        .map((h, i) => ({
          titulo: h.titulo.trim(),
          fechaSugerida: h.fechaSugerida,
          orden: i + 1,
        })),
    };

    setBusy(true);
    try {
      if (editando && plId) {
        await api.catalogo.updatePlantilla(plId, body);
        navigate(`/profesor/asignaturas/${id}/plantillas/${plId}`, { replace: true });
      } else {
        await api.catalogo.createPlantilla(id, body);
        navigate(`/profesor/asignaturas/${id}`, { replace: true });
      }
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : editando
            ? 'No se pudo actualizar la plantilla'
            : 'No se pudo crear la plantilla',
      );
    } finally {
      setBusy(false);
    }
  }

  const cancelTo = editando && plId
    ? `/profesor/asignaturas/${id}/plantillas/${plId}`
    : `/profesor/asignaturas/${id}`;

  return (
    <>
      <div className="page-header">
        <div>
          <Link to={cancelTo} className="muted" style={{ fontSize: '0.9rem' }}>
            ← {asignatura.nombre}
          </Link>
          <h2 style={{ margin: '0.35rem 0 0' }}>
            {editando ? 'Editar plantilla de proyecto' : 'Nueva plantilla de proyecto'}
          </h2>
          {editando && (
            <p className="muted" style={{ margin: '0.35rem 0 0', fontSize: '0.9rem' }}>
              Los cambios se aplicarán a la definición de la plantilla para futuros proyectos.
            </p>
          )}
        </div>
      </div>

      {cargando ? (
        <p>Cargando plantilla…</p>
      ) : (
      <form className="card perfil-form" onSubmit={submit}>
        {error && <div className="alert alert-warn">{error}</div>}

        <div className="form-group">
          <label>Título del proyecto plantilla</label>
          <input type="text" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
        </div>

        <div className="form-group">
          <label>Descripción</label>
          <textarea value={descripcion} onChange={(e) => setDescripcion(e.target.value)} rows={3} />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Fecha inicio sugerida</label>
            <input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Fecha fin sugerida</label>
            <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} />
          </div>
        </div>

        <h3>Tareas sugeridas</h3>
        {tareas.map((t, idx) => (
          <div key={idx} className="card" style={{ marginBottom: '0.75rem', padding: '1rem' }}>
            <div className="form-group">
              <label>Título</label>
              <input
                type="text"
                value={t.titulo}
                onChange={(e) => {
                  const next = [...tareas];
                  next[idx] = { ...t, titulo: e.target.value };
                  setTareas(next);
                }}
              />
            </div>
            <div className="form-group">
              <label>Descripción</label>
              <input
                type="text"
                value={t.descripcion}
                onChange={(e) => {
                  const next = [...tareas];
                  next[idx] = { ...t, descripcion: e.target.value };
                  setTareas(next);
                }}
              />
            </div>
            <div className="form-group">
              <label>Fecha límite sugerida</label>
              <input
                type="date"
                value={t.fechaLimiteSugerida}
                onChange={(e) => {
                  const next = [...tareas];
                  next[idx] = { ...t, fechaLimiteSugerida: e.target.value };
                  setTareas(next);
                }}
              />
            </div>
            {tareas.length > 1 && (
              <button type="button" className="btn btn-secondary" onClick={() => setTareas(tareas.filter((_, i) => i !== idx))}>
                Quitar tarea
              </button>
            )}
          </div>
        ))}
        <button type="button" className="btn btn-secondary" onClick={() => setTareas([...tareas, tareaVacia()])}>
          Añadir tarea
        </button>

        <h3 style={{ marginTop: '1.5rem' }}>Hitos sugeridos</h3>
        {hitos.map((h, idx) => (
          <div key={idx} className="card" style={{ marginBottom: '0.75rem', padding: '1rem' }}>
            <div className="form-group">
              <label>Título</label>
              <input
                type="text"
                value={h.titulo}
                onChange={(e) => {
                  const next = [...hitos];
                  next[idx] = { ...h, titulo: e.target.value };
                  setHitos(next);
                }}
              />
            </div>
            <div className="form-group">
              <label>Fecha</label>
              <input
                type="date"
                value={h.fechaSugerida}
                onChange={(e) => {
                  const next = [...hitos];
                  next[idx] = { ...h, fechaSugerida: e.target.value };
                  setHitos(next);
                }}
              />
            </div>
            {hitos.length > 1 && (
              <button type="button" className="btn btn-secondary" onClick={() => setHitos(hitos.filter((_, i) => i !== idx))}>
                Quitar hito
              </button>
            )}
          </div>
        ))}
        <button type="button" className="btn btn-secondary" onClick={() => setHitos([...hitos, hitoVacio()])}>
          Añadir hito
        </button>

        <div className="perfil-form-actions" style={{ marginTop: '1.5rem' }}>
          <Link to={cancelTo} className="btn btn-secondary">Cancelar</Link>
          <button type="submit" className="btn" disabled={busy}>
            {busy ? 'Guardando…' : editando ? 'Guardar cambios' : 'Crear plantilla'}
          </button>
        </div>
      </form>
      )}
    </>
  );
}
