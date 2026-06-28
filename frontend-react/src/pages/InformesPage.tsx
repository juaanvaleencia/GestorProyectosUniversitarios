import { useEffect, useMemo, useState, type ReactNode } from 'react';
import { Link } from 'react-router-dom';
import Breadcrumbs from '../components/Breadcrumbs';
import { api, type InformeHitoPendiente, type InformesResumen, type InformeTareaPendiente } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { etiquetaEstadoTarea } from '../utils/estadoTarea';

function maxActividad(actividad: InformesResumen['actividadSemanal']): number {
  return Math.max(1, ...actividad.map((d) => d.tareas));
}

function etiquetaFechaCorta(fecha: string): string {
  try {
    return new Date(`${fecha}T12:00:00`).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  } catch {
    return fecha;
  }
}

function claseBadgeTarea(estado: string): string {
  const key = estado.trim().toUpperCase().replace(/-/g, '_');
  const map: Record<string, string> = {
    PENDIENTE: 'informes-badge--pendiente',
    EN_PROGRESO: 'informes-badge--progreso',
    REVISION: 'informes-badge--revision',
    HECHA: 'informes-badge--hecha',
  };
  return map[key] ?? 'informes-badge--default';
}

type DetalleModalProps = {
  open: boolean;
  title: string;
  subtitle?: string;
  onClose: () => void;
  children: ReactNode;
};

function DetalleModal({ open, title, subtitle, onClose, children }: DetalleModalProps) {
  if (!open) return null;
  return (
    <div className="modal-backdrop" onClick={onClose} role="presentation">
      <div
        className="modal card informes-detalle-modal"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="informes-modal-title"
      >
        <div className="modal-header">
          <div>
            <h3 id="informes-modal-title" style={{ margin: 0 }}>{title}</h3>
            {subtitle && <p className="muted informes-modal-subtitle">{subtitle}</p>}
          </div>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Cerrar">
            ×
          </button>
        </div>
        <div className="informes-detalle-scroll">{children}</div>
      </div>
    </div>
  );
}

function ListaTareasPendientes({ items }: { items: InformeTareaPendiente[] }) {
  if (items.length === 0) {
    return (
      <div className="informes-detalle-empty">
        <p>No hay tareas pendientes.</p>
      </div>
    );
  }
  return (
    <ul className="informes-detalle-list">
      {items.map((t) => (
        <li key={t.id} className={`informes-detalle-item informes-detalle-item--tarea ${claseBadgeTarea(t.estado)}`}>
          <div className="informes-detalle-item-main">
            <div className="informes-detalle-content">
              <div className="informes-detalle-title-row">
                <strong className="informes-detalle-title">{t.titulo}</strong>
                <span className={`informes-badge ${claseBadgeTarea(t.estado)}`}>
                  {etiquetaEstadoTarea(t.estado)}
                </span>
              </div>
              <div className="informes-detalle-meta">
                {t.fechaLimite && (
                  <span className="informes-detalle-chip informes-detalle-chip--fecha">
                    Límite {etiquetaFechaCorta(t.fechaLimite)}
                  </span>
                )}
                <Link
                  to={`/proyectos/${t.proyectoId}`}
                  className="informes-detalle-chip informes-detalle-chip--proyecto"
                >
                  {t.proyectoTitulo}
                </Link>
              </div>
            </div>
          </div>
        </li>
      ))}
    </ul>
  );
}

function ListaHitosPendientes({ items }: { items: InformeHitoPendiente[] }) {
  if (items.length === 0) {
    return (
      <div className="informes-detalle-empty">
        <p>No hay hitos pendientes.</p>
      </div>
    );
  }
  return (
    <ul className="informes-detalle-list">
      {items.map((h) => (
        <li key={h.id} className="informes-detalle-item informes-detalle-item--hito">
          <div className="informes-detalle-item-main">
            <div className="informes-detalle-content">
              <div className="informes-detalle-title-row">
                <strong className="informes-detalle-title">{h.titulo}</strong>
                <span className="informes-badge informes-badge--hito">Pendiente</span>
              </div>
              <div className="informes-detalle-meta">
                <span className="informes-detalle-chip informes-detalle-chip--fecha">
                  {etiquetaFechaCorta(h.fecha)}
                </span>
                <Link
                  to={`/proyectos/${h.proyectoId}`}
                  className="informes-detalle-chip informes-detalle-chip--proyecto"
                >
                  {h.proyectoTitulo}
                </Link>
              </div>
            </div>
          </div>
        </li>
      ))}
    </ul>
  );
}

export default function InformesPage() {
  const { user } = useAuth();
  const [data, setData] = useState<InformesResumen | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalTareas, setModalTareas] = useState(false);
  const [modalHitos, setModalHitos] = useState(false);

  useEffect(() => {
    if (!user) return;
    (async () => {
      try {
        setData(await api.informesResumen());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar informes');
      }
    })();
  }, [user]);

  const totalTareas = useMemo(
    () => (data ? data.tareasCompletadas + data.tareasPendientes : 0),
    [data],
  );

  const totalHitos = useMemo(
    () => (data ? data.hitosCompletados + data.hitosPendientes : 0),
    [data],
  );

  const maxBarras = useMemo(
    () => (data ? maxActividad(data.actividadSemanal) : 1),
    [data],
  );

  const sinDatos = data
    && data.proyectosActivos === 0
    && totalTareas === 0
    && totalHitos === 0;

  const actividadVacia = data?.actividadSemanal.every((d) => d.tareas === 0) ?? true;

  if (error) {
    return (
      <>
        <Breadcrumbs items={[{ label: 'Inicio', to: '/' }, { label: 'Informes' }]} />
        <div className="alert alert-warn">{error}. Comprueba que el backend esté en marcha.</div>
      </>
    );
  }

  if (!data) return <p>Cargando informes…</p>;

  return (
    <>
      <Breadcrumbs items={[{ label: 'Inicio', to: '/' }, { label: 'Informes' }]} />

      <div className="page-header">
        <h2>Informes</h2>
        <p>{data.mensaje ?? 'Resumen de actividad de tus proyectos'}</p>
      </div>

      {sinDatos && (
        <div className="card empty-state">
          <p className="muted">Aún no hay datos suficientes para generar estadísticas.</p>
          <Link to="/proyectos/nuevo" className="btn">
            Crear proyecto
          </Link>
        </div>
      )}

      <div className="grid grid-4" style={{ marginBottom: '1.5rem' }}>
        <div className="card stat">
          <strong>{data.proyectosActivos}</strong>
          <span>Proyectos en curso</span>
        </div>
        <div className="card stat">
          <strong>{data.tareasCompletadas}</strong>
          <span>Tareas hechas</span>
        </div>
        <div className="card stat stat-warn">
          <strong>{data.tareasPendientes}</strong>
          <span>Tareas pendientes</span>
        </div>
        <div className="card stat">
          <strong>{data.progresoMedio}%</strong>
          <span>Progreso en tareas</span>
        </div>
      </div>

      <div className="grid grid-2" style={{ marginBottom: '1.5rem' }}>
        <div className="card">
          <h3 style={{ marginTop: 0 }}>Resumen de tareas</h3>
          <p className="muted" style={{ fontSize: '0.9rem', marginTop: 0 }}>
            {totalTareas === 0
              ? 'No tienes tareas en tus proyectos.'
              : `${data.tareasCompletadas} completadas · ${data.tareasPendientes} pendientes (${totalTareas} en total)`}
          </p>
          {totalTareas > 0 && (
            <>
              <div className="informes-leyenda">
                <span><span className="dot dot-ok" aria-hidden /> Hechas ({data.tareasCompletadas})</span>
                <span><span className="dot dot-warn" aria-hidden /> Pendientes ({data.tareasPendientes})</span>
              </div>
              <div className="progress-bar stacked" style={{ marginTop: '0.75rem' }}>
                <div
                  className="progress-bar-fill progress-bar-ok"
                  style={{ width: `${data.progresoMedio}%` }}
                />
              </div>
            </>
          )}
          {data.tareasPendientes > 0 && (
            <button
              type="button"
              className="btn btn-secondary informes-detalle-btn"
              onClick={() => setModalTareas(true)}
            >
              Ver tareas pendientes ({data.tareasPendientes})
            </button>
          )}
        </div>

        <div className="card">
          <h3 style={{ marginTop: 0 }}>Resumen de hitos</h3>
          <p className="muted" style={{ fontSize: '0.9rem', marginTop: 0 }}>
            {totalHitos === 0
              ? 'No hay hitos en tus proyectos.'
              : `${data.hitosCompletados} completados · ${data.hitosPendientes} pendientes (${totalHitos} en total)`}
          </p>
          {totalHitos > 0 && (
            <div className="progress-bar stacked" style={{ marginTop: '0.75rem' }}>
              <div
                className="progress-bar-fill progress-bar-ok"
                style={{
                  width: `${Math.round((data.hitosCompletados / totalHitos) * 100)}%`,
                }}
              />
            </div>
          )}
          {data.hitosPendientes > 0 && (
            <button
              type="button"
              className="btn btn-secondary informes-detalle-btn"
              onClick={() => setModalHitos(true)}
            >
              Ver hitos pendientes ({data.hitosPendientes})
            </button>
          )}
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginTop: 0 }}>Próximos 7 días</h3>
        <p className="muted" style={{ fontSize: '0.9rem', marginTop: 0 }}>
          Tareas pendientes con fecha límite desde hoy durante los siguientes 7 días.
        </p>
        {actividadVacia ? (
          <p className="muted" style={{ marginTop: '1rem' }}>
            No hay tareas con fecha límite en los próximos 7 días.
          </p>
        ) : (
          <div className="informes-chart" aria-hidden="false">
            {data.actividadSemanal.map((d) => {
              const altura = Math.max(8, Math.round((d.tareas / maxBarras) * 120));
              const esHoy = d.fecha === new Date().toISOString().slice(0, 10);
              return (
                <div key={d.fecha} className={`informes-chart-col${esHoy ? ' informes-chart-col--hoy' : ''}`}>
                  {d.tareas > 0 && <span className="informes-chart-value">{d.tareas}</span>}
                  <div className="informes-chart-bar" style={{ height: `${altura}px` }} />
                  <small className="informes-chart-dia">{d.dia}</small>
                  <small className="informes-chart-fecha">{etiquetaFechaCorta(d.fecha)}</small>
                </div>
              );
            })}
          </div>
        )}
      </div>

      <DetalleModal
        open={modalTareas}
        title="Tareas pendientes"
        subtitle={`${data.tareasPendientes} tarea(s) en tus proyectos`}
        onClose={() => setModalTareas(false)}
      >
        <ListaTareasPendientes items={data.tareasPendientesDetalle ?? []} />
      </DetalleModal>

      <DetalleModal
        open={modalHitos}
        title="Hitos pendientes"
        subtitle={`${data.hitosPendientes} hito(s) por completar`}
        onClose={() => setModalHitos(false)}
      >
        <ListaHitosPendientes items={data.hitosPendientesDetalle ?? []} />
      </DetalleModal>
    </>
  );
}
