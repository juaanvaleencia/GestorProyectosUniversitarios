import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Breadcrumbs from '../components/Breadcrumbs';
import { api, type Notificacion } from '../api/client';
import { useAuth } from '../auth/AuthContext';

function formatFecha(value: string): string {
  try {
    return new Date(value).toLocaleString('es-ES', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return value;
  }
}

function mensajeSituacionInvitacion(situacion?: Notificacion['invitacionSituacion']): string | null {
  switch (situacion) {
    case 'ACEPTADA':
      return 'Has aceptado la invitación.';
    case 'RECHAZADA':
      return 'Has rechazado la invitación.';
    case 'OTRO_GRUPO':
      return 'Ya te has unido a este proyecto con otro grupo.';
    case 'YA_MIEMBRO':
      return 'Ya formas parte de este proyecto.';
    default:
      return null;
  }
}

export default function NotificacionesPage() {
  const navigate = useNavigate();
  const { refreshPerfil } = useAuth();
  const [items, setItems] = useState<Notificacion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actingOnId, setActingOnId] = useState<number | null>(null);

  async function load() {
    try {
      setItems(await api.notificaciones.list());
      setError(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, []);

  const noLeidas = useMemo(() => items.filter((n) => !n.leida).length, [items]);

  async function aceptarInvitacion(n: Notificacion) {
    if (!n.invitacionId) return;
    setActingOnId(n.id);
    setError(null);
    try {
      await api.invitaciones.accept(n.invitacionId);
      await refreshPerfil();
      await load();
      if (n.proyectoId) {
        navigate(`/proyectos/${n.proyectoId}`);
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo aceptar la invitación');
    } finally {
      setActingOnId(null);
    }
  }

  async function rechazarInvitacion(n: Notificacion) {
    if (!n.invitacionId) return;
    setActingOnId(n.id);
    setError(null);
    try {
      await api.invitaciones.reject(n.invitacionId);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo rechazar la invitación');
    } finally {
      setActingOnId(null);
    }
  }

  if (loading) return <p>Cargando notificaciones…</p>;

  return (
    <>
      <Breadcrumbs items={[{ label: 'Inicio', to: '/' }, { label: 'Notificaciones' }]} />

      <div className="page-header page-header-row">
        <div>
          <h2>Notificaciones</h2>
          <p className="muted">Avisos asociados a tu cuenta</p>
        </div>
        {noLeidas > 0 && (
          <span className="notif-badge">{noLeidas} sin leer</span>
        )}
      </div>

      {error && <div className="alert alert-warn">{error}</div>}

      {items.length === 0 ? (
        <div className="card notif-empty">
          <div className="notif-empty-icon" aria-hidden>🔔</div>
          <h3>Sin notificaciones</h3>
          <p className="muted">
            Recibirás avisos al iniciar sesión por primera vez o cuando te inviten a un proyecto.
          </p>
        </div>
      ) : (
        <div className="notif-list">
          {items.map((n) => {
            const esInvitacion = n.tipo === 'INVITACION_PROYECTO' && n.invitacionId != null;
            const busy = actingOnId === n.id;
            const situacion = esInvitacion ? n.invitacionSituacion : undefined;
            const accionable = situacion === 'ACCIONABLE' || (esInvitacion && !situacion);
            const mensajeEstado = mensajeSituacionInvitacion(situacion);
            return (
              <article
                key={n.id}
                className={`card notif-item${n.leida ? ' notif-item--read' : ' notif-item--unread'}`}
              >
                <div className="notif-item-marker" aria-hidden />
                <div className="notif-item-body">
                  <p className="notif-item-text">{n.texto}</p>
                  <time className="notif-item-date" dateTime={n.creadoEn}>
                    {formatFecha(n.creadoEn)}
                  </time>
                  {esInvitacion && accionable && (
                    <div className="notif-inv-actions">
                      <button
                        type="button"
                        className="btn"
                        disabled={busy}
                        onClick={() => void aceptarInvitacion(n)}
                      >
                        {busy ? 'Procesando…' : 'Aceptar'}
                      </button>
                      <button
                        type="button"
                        className="btn btn-secondary"
                        disabled={busy}
                        onClick={() => void rechazarInvitacion(n)}
                      >
                        Rechazar
                      </button>
                    </div>
                  )}
                  {esInvitacion && mensajeEstado && (
                    <p
                      className={`notif-inv-estado notif-inv-estado--${(situacion ?? 'default').toLowerCase()}`}
                      role="status"
                    >
                      {mensajeEstado}
                      {situacion === 'ACEPTADA' && n.proyectoId != null && (
                        <>
                          {' '}
                          <Link to={`/proyectos/${n.proyectoId}`}>Ir al proyecto</Link>
                        </>
                      )}
                    </p>
                  )}
                </div>
                {!n.leida && <span className="notif-item-pill">Nueva</span>}
              </article>
            );
          })}
        </div>
      )}
    </>
  );
}
