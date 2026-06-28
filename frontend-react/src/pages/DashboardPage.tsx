import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type Proyecto } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { claseBadgeEstadoProyecto, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { nombreParaMostrar } from '../utils/nombreUsuario';

export default function DashboardPage() {
  const { user, perfil } = useAuth();
  const [proyectos, setProyectos] = useState<Proyecto[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;
    (async () => {
      try {
        setProyectos(await api.proyectos.list());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar');
      }
    })();
  }, [user]);

  const enCurso = useMemo(
    () => proyectos.filter((p) => p.estado === 'EN_CURSO').length,
    [proyectos],
  );
  const planificacion = useMemo(
    () => proyectos.filter((p) => p.estado === 'PLANIFICACION').length,
    [proyectos],
  );
  const recientes = useMemo(() => proyectos.slice(0, 5), [proyectos]);
  const nombre = nombreParaMostrar(perfil, user);

  if (error) {
    return (
      <div className="dashboard-page">
        <div className="alert alert-warn">{error}</div>
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="dashboard-page">
      <header className="dashboard-hero">
        <p className="dashboard-eyebrow">Panel principal</p>
        <h1 className="dashboard-title">¡Hola, {nombre}!</h1>
        <p className="dashboard-subtitle">
          Organiza y sigue el avance de tus proyectos universitarios
          {perfil?.universidadNombre && (
            <> · <span className="dashboard-uni">{perfil.universidadNombre}</span></>
          )}
        </p>
      </header>

      <div className="dashboard-stats">
        <div className="dashboard-stat">
          <span className="dashboard-stat-value">{proyectos.length}</span>
          <span className="dashboard-stat-label">Proyectos</span>
        </div>
        <div className="dashboard-stat">
          <span className="dashboard-stat-value">{enCurso}</span>
          <span className="dashboard-stat-label">En curso</span>
        </div>
        <div className="dashboard-stat">
          <span className="dashboard-stat-value">{planificacion}</span>
          <span className="dashboard-stat-label">Planificación</span>
        </div>
      </div>

      <div className="dashboard-actions">
        <Link to="/proyectos/nuevo" className="btn cta-panel">
          + Nuevo proyecto
        </Link>
      </div>

      <div className="dashboard-grid">
        <section className="card dashboard-panel">
          <h3>Proyectos recientes</h3>
          {recientes.length === 0 ? (
            <p className="muted">Aún no tienes proyectos. Crea uno desde el botón superior.</p>
          ) : (
            <ul className="dashboard-project-list">
              {recientes.map((p) => (
                <li key={p.id}>
                  <Link to={`/proyectos/${p.id}`} className="dashboard-project-link">
                    {p.titulo}
                  </Link>
                  <span className={`badge badge-${claseBadgeEstadoProyecto(p.estado)}`}>
                    {etiquetaEstadoProyecto(p.estado)}
                  </span>
                </li>
              ))}
            </ul>
          )}
          {proyectos.length > 0 && (
            <Link to="/proyectos" className="dashboard-panel-link">
              Ver todos los proyectos →
            </Link>
          )}
        </section>

        <section className="card dashboard-panel">
          <h3>Accesos rápidos</h3>
          <div className="dashboard-quick-links">
            <Link to="/proyectos" className="dashboard-quick-card">
              <span className="dashboard-quick-title">Mis proyectos</span>
              <span className="muted">Listado y filtros</span>
            </Link>
            <Link to="/informes" className="dashboard-quick-card">
              <span className="dashboard-quick-title">Informes</span>
              <span className="muted">Estadísticas y progreso</span>
            </Link>
            <Link to="/notificaciones" className="dashboard-quick-card">
              <span className="dashboard-quick-title">Notificaciones</span>
              <span className="muted">Avisos y recordatorios</span>
            </Link>
          </div>
        </section>
      </div>
    </div>
  );
}
