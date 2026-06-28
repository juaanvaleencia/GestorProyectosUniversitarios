import { useEffect, useState } from 'react';
import { Link, Navigate, useParams } from 'react-router-dom';
import { api, type PlantillaProyectoDetalle, type ProyectoGrupo, type ProfesorNavState } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { claseBadgeEstadoProyecto, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { etiquetaRol } from '../utils/rolProyecto';

function formatDate(value?: string) {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleDateString('es-ES');
  } catch {
    return value;
  }
}

export default function ProfesorPlantillaDetailPage() {
  const { asignaturaId, plantillaId } = useParams();
  const asigId = Number(asignaturaId);
  const plId = Number(plantillaId);
  const { perfil } = useAuth();
  const [plantilla, setPlantilla] = useState<PlantillaProyectoDetalle | null>(null);
  const [grupos, setGrupos] = useState<ProyectoGrupo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const asignatura = (perfil?.asignaturasImpartidas ?? []).find((a) => a.id === asigId);

  async function cargar() {
    setError(null);
    try {
      const [det, gr] = await Promise.all([
        api.catalogo.plantilla(plId),
        api.profesor.gruposPorPlantilla(plId),
      ]);
      setPlantilla(det);
      setGrupos(gr);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar');
    }
  }

  useEffect(() => {
    if (!asignatura || Number.isNaN(plId)) return;
    (async () => {
      setLoading(true);
      await cargar();
      setLoading(false);
    })();
    const interval = setInterval(() => void cargar(), 45000);
    return () => clearInterval(interval);
  }, [asignatura, plId]);

  if (!perfil) return <p>Cargando…</p>;
  if (!asignatura || Number.isNaN(asigId) || Number.isNaN(plId)) {
    return <Navigate to="/profesor" replace />;
  }

  const navState: ProfesorNavState = {
    asignaturaId: asigId,
    asignaturaNombre: asignatura.nombre,
    plantillaId: plId,
    plantillaTitulo: plantilla?.titulo ?? 'Plantilla',
  };

  return (
    <div className="profesor-plantilla-detail">
      <div className="page-header page-header-row">
        <div>
          <Link to={`/profesor/asignaturas/${asigId}`} className="muted profesor-back-link">
            ← {asignatura.nombre}
          </Link>
          <h2 style={{ margin: '0.35rem 0 0' }}>{plantilla?.titulo ?? 'Plantilla'}</h2>
          {plantilla?.descripcion && <p className="muted">{plantilla.descripcion}</p>}
        </div>
        <Link
          to={`/profesor/asignaturas/${asigId}/plantillas/${plId}/editar`}
          className="btn btn-secondary"
        >
          Editar plantilla
        </Link>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {loading && <p>Cargando…</p>}

      {!loading && plantilla && (
        <>
          <div className="card profesor-plantilla-info">
            <h3 style={{ marginTop: 0 }}>Definición de la plantilla</h3>
            <dl className="project-meta project-meta-inline">
              <div>
                <dt>Inicio sugerido</dt>
                <dd>{formatDate(plantilla.fechaInicioSugerida)}</dd>
              </div>
              <div>
                <dt>Fin sugerido</dt>
                <dd>{formatDate(plantilla.fechaFinSugerida)}</dd>
              </div>
              <div>
                <dt>Tareas plantilla</dt>
                <dd>{plantilla.tareas.length}</dd>
              </div>
              <div>
                <dt>Hitos plantilla</dt>
                <dd>{plantilla.hitos.length}</dd>
              </div>
            </dl>
            {(plantilla.tareas.length > 0 || plantilla.hitos.length > 0) && (
              <div className="profesor-plantilla-preview">
                {plantilla.tareas.length > 0 && (
                  <div>
                    <h4>Tareas sugeridas</h4>
                    <ul className="profesor-preview-list">
                      {plantilla.tareas.map((t) => (
                        <li key={t.id}>{t.titulo}</li>
                      ))}
                    </ul>
                  </div>
                )}
                {plantilla.hitos.length > 0 && (
                  <div>
                    <h4>Hitos sugeridos</h4>
                    <ul className="profesor-preview-list">
                      {plantilla.hitos.map((h) => (
                        <li key={h.id}>{h.titulo} · {formatDate(h.fechaSugerida)}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            )}
          </div>

          <section className="card">
            <div className="profesor-panel-header">
              <h3 style={{ margin: 0 }}>Grupos de trabajo</h3>
              <p className="muted" style={{ margin: '0.25rem 0 0' }}>
                Pulsa en un grupo para supervisar su proyecto. Los participantes enlazan a su perfil.
              </p>
            </div>

            {grupos.length === 0 ? (
              <p className="muted">Ningún alumno ha creado aún un proyecto con esta plantilla.</p>
            ) : (
              <div className="profesor-grupos-list">
                {grupos.map((g) => (
                  <Link
                    key={g.id}
                    to={`/profesor/proyectos/${g.id}`}
                    state={navState}
                    className="profesor-grupo-card profesor-grupo-card--link"
                  >
                    <div className="profesor-grupo-header">
                      <div>
                        <span className="profesor-grupo-titulo">{g.titulo}</span>
                        <p className="muted" style={{ margin: '0.2rem 0 0', fontSize: '0.85rem' }}>
                          Proyecto #{g.id} · Actualizado {formatDate(g.actualizadoEn)}
                        </p>
                      </div>
                      <span className={`badge ${claseBadgeEstadoProyecto(g.estado)}`}>
                        {etiquetaEstadoProyecto(g.estado)}
                      </span>
                    </div>
                    <div className="profesor-grupo-participantes">
                      {g.participantes.map((p) => (
                        p.uid ? (
                          <Link
                            key={`${g.id}-${p.uid}`}
                            to={`/profesor/alumnos/${encodeURIComponent(p.uid)}`}
                            state={navState}
                            className="profesor-participante-chip profesor-participante-chip--link"
                            onClick={(e) => e.stopPropagation()}
                          >
                            <strong>{p.nombre}</strong>
                            <small>
                              {etiquetaRol(p.rol, p.rolEtiqueta)}
                              {p.propietario ? ' · Propietario' : ''}
                            </small>
                          </Link>
                        ) : (
                          <span key={`${g.id}-${p.email}`} className="profesor-participante-chip">
                            <strong>{p.nombre}</strong>
                            <small>
                              {etiquetaRol(p.rol, p.rolEtiqueta)}
                              {p.propietario ? ' · Propietario' : ''}
                            </small>
                          </span>
                        )
                      ))}
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
