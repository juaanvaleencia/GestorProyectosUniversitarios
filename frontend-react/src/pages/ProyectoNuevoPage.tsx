import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  api,
  type Asignatura,
  type PlantillaProyecto,
  type PlantillaProyectoDetalle,
} from '../api/client';
import Breadcrumbs from '../components/Breadcrumbs';
import { useAuth } from '../auth/AuthContext';
import { ESTADOS_PROYECTO, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { etiquetaTutor } from '../utils/tutorAsignatura';

type Modo = 'eleccion' | 'libre' | 'asignaturas' | 'plantillas' | 'detalle';

function formatDate(value?: string) {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleDateString('es-ES');
  } catch {
    return value;
  }
}

export default function ProyectoNuevoPage() {
  const navigate = useNavigate();
  const { perfil } = useAuth();
  const [modo, setModo] = useState<Modo>('eleccion');
  const [asignaturas, setAsignaturas] = useState<Asignatura[]>([]);
  const [asignaturaSel, setAsignaturaSel] = useState<Asignatura | null>(null);
  const [plantillas, setPlantillas] = useState<PlantillaProyecto[]>([]);
  const [detalle, setDetalle] = useState<PlantillaProyectoDetalle | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [estado, setEstado] = useState('PLANIFICACION');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (modo !== 'asignaturas') return;
    setLoading(true);
    setError(null);
    const matriculadas = perfil?.asignaturasMatriculadas ?? [];
    if (matriculadas.length === 0) {
      setAsignaturas([]);
      setLoading(false);
      return;
    }
    setAsignaturas(matriculadas);
    setLoading(false);
  }, [modo, perfil?.asignaturasMatriculadas]);

  async function elegirAsignatura(asignatura: Asignatura) {
    setLoading(true);
    setError(null);
    try {
      setAsignaturaSel(asignatura);
      setPlantillas(await api.catalogo.plantillas(asignatura.id));
      setModo('plantillas');
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar proyectos de la asignatura');
    } finally {
      setLoading(false);
    }
  }

  async function elegirPlantilla(plantillaId: number) {
    setLoading(true);
    setError(null);
    try {
      setDetalle(await api.catalogo.plantilla(plantillaId));
      setModo('detalle');
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar la plantilla');
    } finally {
      setLoading(false);
    }
  }

  async function crearDesdePlantilla() {
    if (!detalle) return;
    setSaving(true);
    setError(null);
    try {
      const created = await api.proyectos.createFromPlantilla(detalle.id);
      navigate(`/proyectos/${created.id}`, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el proyecto');
    } finally {
      setSaving(false);
    }
  }

  async function submitLibre(e: FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const created = await api.proyectos.create({
        titulo,
        descripcion,
        fechaInicio: fechaInicio || undefined,
        fechaFin: fechaFin || undefined,
        estado,
      });
      navigate(`/proyectos/${created.id}`, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  const breadcrumbLabel =
    modo === 'eleccion'
      ? 'Nuevo proyecto'
      : modo === 'libre'
        ? 'Proyecto libre'
        : modo === 'asignaturas'
          ? 'Desde mi universidad'
          : modo === 'plantillas'
            ? (asignaturaSel?.nombre ?? 'Asignatura')
            : (detalle?.titulo ?? 'Plantilla');

  return (
    <>
      <Breadcrumbs
        items={[
          { label: 'Inicio', to: '/' },
          { label: 'Proyectos', to: '/proyectos' },
          { label: breadcrumbLabel },
        ]}
      />

      <div className="page-header" style={{ textAlign: 'center', maxWidth: 720, margin: '0 auto 1.25rem' }}>
        <h2>Nuevo proyecto</h2>
        <p className="muted">
          {modo === 'eleccion'
            ? 'Crea un proyecto desde cero o elige una asignatura de tu universidad.'
            : modo === 'libre'
              ? 'Completa los datos del proyecto.'
              : modo === 'asignaturas'
                ? `Asignaturas de ${perfil?.universidadNombre ?? 'tu universidad'}.`
                : modo === 'plantillas'
                  ? `Proyectos del curso en ${asignaturaSel?.nombre}.`
                  : 'Revisa la plantilla antes de crear el proyecto.'}
        </p>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {loading && <p className="muted">Cargando…</p>}

      {modo === 'eleccion' && (
        <div className="choice-grid-centered">
          <button
            type="button"
            className="card choice-card"
            onClick={() => setModo('libre')}
          >
            <h3 style={{ marginTop: 0 }}>Proyecto libre</h3>
            <p className="muted" style={{ marginBottom: 0 }}>
              Define título, fechas y tareas sin plantilla. Para TFG, prácticas u otros trabajos.
            </p>
          </button>
          <button
            type="button"
            className="card choice-card"
            onClick={() => {
              if (!perfil?.universidadId) {
                setError('Indica tu universidad en el perfil para ver asignaturas.');
                return;
              }
              setModo('asignaturas');
            }}
          >
            <h3 style={{ marginTop: 0 }}>Desde mi universidad</h3>
            <p className="muted" style={{ marginBottom: 0 }}>
              Elige una asignatura y un proyecto del curso con tareas e hitos precargados.
            </p>
          </button>
        </div>
      )}

      {!loading && modo === 'asignaturas' && (
        <>
          <button type="button" className="btn btn-secondary" onClick={() => setModo('eleccion')}>
            ← Volver
          </button>
          {asignaturas.length === 0 ? (
            <div className="card" style={{ marginTop: '1rem', textAlign: 'center' }}>
              <p className="muted" style={{ marginBottom: '1rem' }}>
                No tienes asignaturas matriculadas. Configúralas en tu perfil para crear proyectos del curso.
              </p>
              <Link to="/perfil" className="btn btn-secondary">
                Ir a matrículas
              </Link>
            </div>
          ) : (
          <div className="grid grid-2" style={{ marginTop: '1rem' }}>
            {asignaturas.map((a) => (
              <button
                key={a.id}
                type="button"
                className="card"
                style={{ textAlign: 'left', cursor: 'pointer' }}
                onClick={() => elegirAsignatura(a)}
              >
                <h3 style={{ marginTop: 0 }}>{a.nombre}</h3>
                {a.descripcion && (
                  <p className="muted" style={{ fontSize: '0.9rem' }}>{a.descripcion}</p>
                )}
                <p style={{ marginBottom: 0, fontSize: '0.85rem' }}>
                  Tutor: <strong>{etiquetaTutor(a.tutorNombre)}</strong>
                </p>
              </button>
            ))}
          </div>
          )}
        </>
      )}

      {!loading && modo === 'plantillas' && (
        <>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setAsignaturaSel(null);
              setPlantillas([]);
              setModo('asignaturas');
            }}
          >
            ← Volver a asignaturas
          </button>
          <div className="card" style={{ marginTop: '1rem' }}>
            <p className="muted" style={{ margin: 0 }}>
              Tutor: {etiquetaTutor(asignaturaSel?.tutorNombre)}
            </p>
          </div>
          {plantillas.length === 0 ? (
            <div className="card" style={{ marginTop: '1rem', textAlign: 'center' }}>
              <p className="muted" style={{ marginBottom: 0 }}>
                No hay plantillas disponibles en esta asignatura. Ya participas en todos los proyectos del curso
                o el profesor aún no ha publicado plantillas.
              </p>
            </div>
          ) : (
          <ul style={{ marginTop: '1rem', paddingLeft: '1.2rem' }}>
            {plantillas.map((p) => (
              <li key={p.id} style={{ marginBottom: '0.75rem' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  style={{ marginRight: '0.5rem' }}
                  onClick={() => elegirPlantilla(p.id)}
                >
                  Ver plantilla
                </button>
                <strong>{p.titulo}</strong>
                <span className="muted" style={{ marginLeft: '0.5rem', fontSize: '0.85rem' }}>
                  {p.numTareas} tareas · {p.numHitos} hitos
                </span>
              </li>
            ))}
          </ul>
          )}
        </>
      )}

      {!loading && modo === 'detalle' && detalle && (
        <>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setDetalle(null);
              setModo('plantillas');
            }}
          >
            ← Volver a proyectos de la asignatura
          </button>
          <div className="card" style={{ marginTop: '1rem' }}>
            <h3 style={{ marginTop: 0 }}>{detalle.titulo}</h3>
            <p className="muted">{detalle.descripcion}</p>
            <p style={{ fontSize: '0.9rem' }}>
              <span className="muted">Asignatura:</span> {detalle.asignaturaNombre}
              <br />
              <span className="muted">Tutor:</span> {etiquetaTutor(detalle.tutorNombre)}
              <br />
              <span className="muted">Fechas sugeridas:</span>{' '}
              {formatDate(detalle.fechaInicioSugerida)} — {formatDate(detalle.fechaFinSugerida)}
            </p>
            <button type="button" className="btn" onClick={crearDesdePlantilla} disabled={saving}>
              {saving ? 'Creando proyecto…' : 'Crear proyecto con esta plantilla'}
            </button>
            <p className="muted" style={{ fontSize: '0.85rem', marginTop: '0.75rem', marginBottom: 0 }}>
              Se creará el proyecto con sus tareas e hitos y se añadirá al tutor de la asignatura.
            </p>
          </div>
          <div className="grid grid-2" style={{ marginTop: '1rem' }}>
            <div className="card">
              <h4 style={{ marginTop: 0 }}>Tareas ({detalle.tareas.length})</h4>
              <ul style={{ paddingLeft: '1.2rem', margin: 0 }}>
                {detalle.tareas.map((t) => (
                  <li key={t.id} style={{ marginBottom: '0.35rem' }}>
                    {t.titulo}
                    {t.fechaLimiteSugerida && (
                      <span className="muted" style={{ fontSize: '0.8rem' }}>
                        {' '}
                        · {formatDate(t.fechaLimiteSugerida)}
                      </span>
                    )}
                  </li>
                ))}
              </ul>
            </div>
            <div className="card">
              <h4 style={{ marginTop: 0 }}>Hitos ({detalle.hitos.length})</h4>
              <ul style={{ paddingLeft: '1.2rem', margin: 0 }}>
                {detalle.hitos.map((h) => (
                  <li key={h.id} style={{ marginBottom: '0.35rem' }}>
                    {h.titulo}
                    <span className="muted" style={{ fontSize: '0.8rem' }}>
                      {' '}
                      · {formatDate(h.fechaSugerida)}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </>
      )}

      {modo === 'libre' && (
        <div className="form-centered-wrap">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => setModo(detalle ? 'detalle' : 'eleccion')}
            style={{ marginBottom: '1rem' }}
          >
            ← Volver
          </button>
          <form className="card form-centered" onSubmit={submitLibre}>
            <div className="form-group">
              <label>Título</label>
              <input value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Descripción</label>
              <textarea
                rows={4}
                value={descripcion}
                onChange={(e) => setDescripcion(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Fecha inicio</label>
              <input
                type="date"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Fecha fin</label>
              <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Estado</label>
              <select value={estado} onChange={(e) => setEstado(e.target.value)}>
                {ESTADOS_PROYECTO.map((value) => (
                  <option key={value} value={value}>
                    {etiquetaEstadoProyecto(value)}
                  </option>
                ))}
              </select>
            </div>
            <button type="submit" className="btn" disabled={saving}>
              {saving ? 'Guardando…' : 'Crear proyecto'}
            </button>
            <Link to="/proyectos" className="btn btn-secondary" style={{ marginLeft: '0.5rem' }}>
              Cancelar
            </Link>
          </form>
        </div>
      )}
    </>
  );
}
