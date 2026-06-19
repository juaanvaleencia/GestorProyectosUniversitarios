import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api, type Hito, type Miembro, type Proyecto, type Tarea } from '../api/client';
import GanttChart from '../components/GanttChart';
import { etiquetaRol } from '../utils/rolProyecto';

type Tab = 'tareas' | 'cronograma' | 'equipo';

const ESTADOS_KANBAN = ['PENDIENTE', 'EN_PROGRESO', 'REVISION', 'HECHA'];

export default function ProyectoDetailPage() {
  const { id } = useParams();
  const proyectoId = Number(id);
  const [tab, setTab] = useState<Tab>('tareas');
  const [proyecto, setProyecto] = useState<Proyecto | null>(null);
  const [tareas, setTareas] = useState<Tarea[]>([]);
  const [hitos, setHitos] = useState<Hito[]>([]);
  const [miembros, setMiembros] = useState<Miembro[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [nuevaTarea, setNuevaTarea] = useState('');
  const [addingTarea, setAddingTarea] = useState(false);

  async function reloadTareas() {
    setTareas(await api.tareas.list(proyectoId));
  }

  async function addTarea() {
    const titulo = nuevaTarea.trim();
    if (!titulo) return;
    setAddingTarea(true);
    try {
      await api.tareas.create(proyectoId, {
        titulo,
        estado: 'PENDIENTE',
        prioridad: 'MEDIA',
        orden: tareas.length + 1,
      });
      setNuevaTarea('');
      await reloadTareas();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo crear la tarea');
    } finally {
      setAddingTarea(false);
    }
  }

  async function removeTarea(tareaId: number) {
    try {
      await api.tareas.delete(proyectoId, tareaId);
      await reloadTareas();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar la tarea');
    }
  }

  useEffect(() => {
    if (Number.isNaN(proyectoId)) {
      setError('Proyecto no válido');
      return;
    }
    (async () => {
      setError(null);
      setProyecto(null);
      try {
        setProyecto(await api.proyectos.get(proyectoId));
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar el proyecto');
        return;
      }
      try {
        setTareas(await api.tareas.list(proyectoId));
        setHitos(await api.hitos(proyectoId));
        setMiembros(await api.miembros(proyectoId));
      } catch {
        /* pestañas secundarias: el detalle principal ya está */
      }
    })();
  }, [proyectoId]);

  function nombreMiembro(uid?: string) {
    if (!uid) return 'Sin asignar';
    return miembros.find((m) => m.usuarioUid === uid)?.nombre ?? uid;
  }

  if (error) return <div className="alert alert-warn">{error}</div>;
  if (!proyecto) return <p>Cargando proyecto…</p>;

  return (
    <>
      <div className="page-header">
        <h2>{proyecto.titulo}</h2>
        <p>{proyecto.descripcion}</p>
        <Link to={`/proyectos/${proyectoId}/editar`}>Editar proyecto</Link>
      </div>

      <div className="tabs">
        <button type="button" className={tab === 'tareas' ? 'active' : ''} onClick={() => setTab('tareas')}>
          Tareas
        </button>
        <button type="button" className={tab === 'cronograma' ? 'active' : ''} onClick={() => setTab('cronograma')}>
          Cronograma
        </button>
        <button type="button" className={tab === 'equipo' ? 'active' : ''} onClick={() => setTab('equipo')}>
          Equipo
        </button>
      </div>

      {tab === 'tareas' && (
        <>
          <div className="card">
            <input
              placeholder="Nueva tarea…"
              value={nuevaTarea}
              onChange={(e) => setNuevaTarea(e.target.value)}
              style={{ width: '100%' }}
            />
            <button
              type="button"
              className="btn"
              disabled={addingTarea || !nuevaTarea.trim()}
              style={{ marginTop: '0.5rem' }}
              onClick={addTarea}
            >
              {addingTarea ? 'Añadiendo…' : 'Añadir tarea'}
            </button>
          </div>
          <div className="kanban">
            {ESTADOS_KANBAN.map((estado) => (
              <div key={estado} className="kanban-col">
                <h4>{estado.replace('_', ' ')}</h4>
                {tareas
                  .filter((t) => t.estado === estado)
                  .map((t) => (
                    <div key={t.id} className="kanban-card">
                      <div>{t.titulo}</div>
                      <small style={{ color: 'var(--muted)' }}>{t.prioridad}</small>
                      <div className="form-group" style={{ marginTop: '0.5rem' }}>
                        <label style={{ fontSize: '0.75rem' }}>Responsable</label>
                        <select value={t.responsableUid ?? ''} disabled>
                          <option value="">Sin asignar</option>
                          {miembros.map((m) => (
                            <option key={m.usuarioUid} value={m.usuarioUid}>
                              {m.nombre}
                            </option>
                          ))}
                        </select>
                      </div>
                      <small>{nombreMiembro(t.responsableUid)}</small>
                      <button
                        type="button"
                        className="btn btn-secondary"
                        style={{ marginTop: '0.5rem', fontSize: '0.75rem' }}
                        onClick={() => removeTarea(t.id)}
                      >
                        Eliminar
                      </button>
                    </div>
                  ))}
              </div>
            ))}
          </div>
        </>
      )}

      {tab === 'cronograma' && (
        <div className="card">
          <h3 style={{ marginTop: 0 }}>Cronograma</h3>
          <div className="timeline" style={{ marginBottom: '1rem' }}>
            {hitos.map((h) => (
              <div key={h.id} className="timeline-item">
                <strong>{h.titulo}</strong>
                <div style={{ color: 'var(--muted)', fontSize: '0.85rem' }}>
                  {h.fecha} {h.completado ? '✓' : '—'}
                </div>
              </div>
            ))}
          </div>
          <GanttChart proyecto={proyecto} hitos={hitos} tareas={tareas} />
        </div>
      )}

      {tab === 'equipo' && (
        <>
          <table className="table card">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Email</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              {miembros.map((m) => (
                <tr key={m.id}>
                  <td>{m.nombre}</td>
                  <td>{m.email}</td>
                  <td>{etiquetaRol(m.rol, m.rolEtiqueta)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="card" style={{ opacity: 0.85 }}>
            <h3>Invitar miembro</h3>
            <p className="muted" style={{ fontSize: '0.85rem' }}>
              En el prototipo el formulario no guarda (solo se muestra la pantalla).
            </p>
            <div className="form-group" style={{ marginTop: '1rem' }}>
              <label>Email</label>
              <input type="email" placeholder="compañero@upsa.es" disabled />
            </div>
            <button type="button" className="btn" disabled>
              Enviar invitación
            </button>
          </div>
        </>
      )}
    </>
  );
}
