import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import Breadcrumbs from '../components/Breadcrumbs';
import { api, type Proyecto } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { ESTADOS_PROYECTO, claseBadgeEstadoProyecto, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { nombreParaMostrar } from '../utils/nombreUsuario';

type SortKey = 'titulo' | 'estado' | 'fechaInicio' | 'actualizadoEn';

function compareProyectos(a: Proyecto, b: Proyecto, sortKey: SortKey): number {
  switch (sortKey) {
    case 'titulo':
      return a.titulo.localeCompare(b.titulo, 'es');
    case 'estado':
      return a.estado.localeCompare(b.estado, 'es');
    case 'fechaInicio':
      return (a.fechaInicio ?? '').localeCompare(b.fechaInicio ?? '');
    case 'actualizadoEn':
      return (b.actualizadoEn ?? '').localeCompare(a.actualizadoEn ?? '');
    default:
      return 0;
  }
}

function formatDate(value?: string) {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleDateString('es-ES');
  } catch {
    return value;
  }
}

export default function ProyectosPage() {
  const { user, perfil } = useAuth();
  const [proyectos, setProyectos] = useState<Proyecto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroAsignatura, setFiltroAsignatura] = useState('');
  const [orden, setOrden] = useState<SortKey>('actualizadoEn');

  useEffect(() => {
    (async () => {
      try {
        setProyectos(await api.proyectos.list());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar proyectos');
      } finally {
        setLoading(false);
      }
    })();
  }, [user]);

  const nombreUsuario = nombreParaMostrar(perfil, user);

  const asignaturasFiltro = useMemo(
    () => perfil?.asignaturasMatriculadas ?? [],
    [perfil?.asignaturasMatriculadas],
  );

  const proyectosFiltrados = useMemo(() => {
    const term = busqueda.trim().toLowerCase();
    return [...proyectos]
      .filter((p) => {
        if (filtroEstado && p.estado !== filtroEstado) return false;
        if (filtroAsignatura && String(p.asignaturaId ?? '') !== filtroAsignatura) return false;
        if (!term) return true;
        const haystack = `${p.titulo} ${p.descripcion ?? ''} ${p.asignaturaNombre ?? ''}`.toLowerCase();
        return haystack.includes(term);
      })
      .sort((a, b) => compareProyectos(a, b, orden));
  }, [proyectos, busqueda, filtroEstado, filtroAsignatura, orden]);

  return (
    <div className="page-proyectos">
      <Breadcrumbs items={[{ label: 'Inicio', to: '/' }, { label: 'Proyectos' }]} />

      <div className="page-header page-header-row">
        <div>
          <h2>Proyectos</h2>
          <p>Listado de proyectos de {nombreUsuario}</p>
        </div>
        <div className="page-header-cta">
          <Link to="/proyectos/nuevo" className="cta-panel-inline">
            + Nuevo proyecto
          </Link>
        </div>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}

      {!loading && proyectos.length > 0 && (
        <div className="card toolbar">
          <div className="toolbar-row">
            <div className="form-group" style={{ marginBottom: 0, flex: 1 }}>
              <label htmlFor="busqueda-proyectos">Buscar</label>
              <input
                id="busqueda-proyectos"
                type="search"
                placeholder="Título o descripción…"
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
            <div className="form-group" style={{ marginBottom: 0, minWidth: 180 }}>
              <label htmlFor="filtro-estado">Estado</label>
              <select
                id="filtro-estado"
                value={filtroEstado}
                onChange={(e) => setFiltroEstado(e.target.value)}
              >
                <option value="">Todos</option>
                {ESTADOS_PROYECTO.map((estado) => (
                  <option key={estado} value={estado}>
                    {etiquetaEstadoProyecto(estado)}
                  </option>
                ))}
              </select>
            </div>
            {asignaturasFiltro.length > 0 && (
              <div className="form-group" style={{ marginBottom: 0, minWidth: 200 }}>
                <label htmlFor="filtro-asignatura">Asignatura</label>
                <select
                  id="filtro-asignatura"
                  value={filtroAsignatura}
                  onChange={(e) => setFiltroAsignatura(e.target.value)}
                >
                  <option value="">Todas</option>
                  {asignaturasFiltro.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.nombre}
                    </option>
                  ))}
                </select>
              </div>
            )}
            <div className="form-group" style={{ marginBottom: 0, minWidth: 180 }}>
              <label htmlFor="orden-proyectos">Ordenar por</label>
              <select
                id="orden-proyectos"
                value={orden}
                onChange={(e) => setOrden(e.target.value as SortKey)}
              >
                <option value="actualizadoEn">Última actualización</option>
                <option value="titulo">Título</option>
                <option value="estado">Estado</option>
                <option value="fechaInicio">Fecha inicio</option>
              </select>
            </div>
          </div>
          <p className="muted toolbar-summary">
            {proyectosFiltrados.length} de {proyectos.length} proyecto(s)
          </p>
        </div>
      )}

      {loading ? (
        <p>Cargando…</p>
      ) : proyectos.length === 0 ? (
        <div className="card card-tone-c empty-state">
          <h3 style={{ marginTop: 0 }}>Sin proyectos</h3>
          <p className="muted">Aún no tienes proyectos. Crea el primero para empezar a planificar.</p>
          <Link to="/proyectos/nuevo" className="cta-panel-inline" style={{ marginTop: '0.75rem' }}>
            + Crear proyecto
          </Link>
        </div>
      ) : proyectosFiltrados.length === 0 ? (
        <div className="card empty-state">
          <h3 style={{ marginTop: 0 }}>Sin resultados</h3>
          <p className="muted">Ningún proyecto coincide con la búsqueda o el filtro seleccionado.</p>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setBusqueda('');
              setFiltroEstado('');
              setFiltroAsignatura('');
            }}
          >
            Limpiar filtros
          </button>
        </div>
      ) : (
        <div className="projects-grid">
          {proyectosFiltrados.map((p) => (
            <Link
              key={p.id}
              to={`/proyectos/${p.id}`}
              className="card project-card project-card-link"
            >
              <div className="project-card-header">
                <h3 style={{ margin: 0 }}>{p.titulo}</h3>
                <span className={`badge badge-estado badge-${claseBadgeEstadoProyecto(p.estado)}`}>
                  {etiquetaEstadoProyecto(p.estado)}
                </span>
              </div>
              <p className="project-card-desc">
                {p.descripcion?.trim() ? p.descripcion : 'Sin descripción'}
              </p>
              <dl className="project-meta">
                {p.asignaturaNombre && (
                  <div>
                    <dt>Asignatura</dt>
                    <dd>{p.asignaturaNombre}</dd>
                  </div>
                )}
                <div>
                  <dt>Inicio</dt>
                  <dd>{formatDate(p.fechaInicio)}</dd>
                </div>
                <div>
                  <dt>Fin</dt>
                  <dd>{formatDate(p.fechaFin)}</dd>
                </div>
                <div>
                  <dt>Actualizado</dt>
                  <dd>{formatDate(p.actualizadoEn?.slice(0, 10))}</dd>
                </div>
              </dl>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
