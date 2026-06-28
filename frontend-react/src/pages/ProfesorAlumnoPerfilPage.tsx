import { useEffect, useState } from 'react';
import { Link, Navigate, useLocation, useParams } from 'react-router-dom';
import { api, type AlumnoPerfilSupervision, type ProfesorNavState } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { claseBadgeEstadoProyecto, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { etiquetaRol } from '../utils/rolProyecto';

export default function ProfesorAlumnoPerfilPage() {
  const { uid } = useParams();
  const location = useLocation();
  const nav = (location.state as ProfesorNavState | null) ?? {};
  const { perfil } = useAuth();
  const [alumno, setAlumno] = useState<AlumnoPerfilSupervision | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!uid) return;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        setAlumno(await api.profesor.alumnoPerfil(uid));
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudo cargar el perfil');
      } finally {
        setLoading(false);
      }
    })();
  }, [uid]);

  if (!perfil) return <p>Cargando…</p>;
  if (!uid) return <Navigate to="/profesor" replace />;

  const backTo = nav.plantillaId && nav.asignaturaId
    ? `/profesor/asignaturas/${nav.asignaturaId}/plantillas/${nav.plantillaId}`
    : nav.asignaturaId
      ? `/profesor/asignaturas/${nav.asignaturaId}`
      : '/profesor';

  const backLabel = nav.plantillaTitulo ?? nav.asignaturaNombre ?? 'Portal del tutor';

  return (
    <div className="profesor-alumno-perfil">
      <div className="page-header">
        <Link to={backTo} className="muted profesor-back-link">← {backLabel}</Link>
        <h2 style={{ margin: '0.35rem 0 0' }}>Perfil del alumno</h2>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {loading && <p>Cargando perfil…</p>}

      {!loading && alumno && (
        <>
          <div className="card profesor-alumno-resumen">
            <div className="profesor-alumno-resumen-top">
              {alumno.avatarUrl ? (
                <img src={alumno.avatarUrl} alt="" className="profesor-alumno-avatar" />
              ) : (
                <div className="profesor-alumno-avatar profesor-alumno-avatar--placeholder">
                  {alumno.nombre.charAt(0).toUpperCase()}
                </div>
              )}
              <div>
                <h3 style={{ margin: 0 }}>{alumno.nombre}</h3>
                <p className="muted" style={{ margin: '0.25rem 0 0' }}>{alumno.email}</p>
                {alumno.universidadNombre && (
                  <p className="muted" style={{ margin: '0.25rem 0 0', fontSize: '0.9rem' }}>
                    {alumno.universidadNombre}
                  </p>
                )}
              </div>
            </div>
          </div>

          <section className="card">
            <h3 style={{ marginTop: 0 }}>Asignaturas matriculadas</h3>
            {alumno.asignaturasMatriculadas.length === 0 ? (
              <p className="muted">Sin asignaturas registradas.</p>
            ) : (
              <ul className="profesor-preview-list">
                {alumno.asignaturasMatriculadas.map((a) => (
                  <li key={a.id}>
                    <strong>{a.nombre}</strong>
                    {a.descripcion && <span className="muted"> — {a.descripcion}</span>}
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section className="card">
            <h3 style={{ marginTop: 0 }}>Proyectos en tus asignaturas</h3>
            {alumno.participaciones.length === 0 ? (
              <p className="muted">Este alumno aún no participa en proyectos de tus asignaturas.</p>
            ) : (
              <div className="profesor-alumno-proyectos">
                {alumno.participaciones.map((p) => (
                  <Link
                    key={p.proyectoId}
                    to={`/profesor/proyectos/${p.proyectoId}`}
                    state={nav}
                    className="card profesor-alumno-proyecto-card"
                  >
                    <div className="profesor-grupo-header">
                      <div>
                        <strong>{p.titulo}</strong>
                        <p className="muted" style={{ margin: '0.2rem 0 0', fontSize: '0.85rem' }}>
                          {etiquetaRol(p.rol, p.rolEtiqueta)}
                        </p>
                      </div>
                      <span className={`badge ${claseBadgeEstadoProyecto(p.estado)}`}>
                        {etiquetaEstadoProyecto(p.estado)}
                      </span>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}
