import { useEffect, useState } from 'react';
import { Link, Navigate, useParams } from 'react-router-dom';
import { api, type PlantillaProyecto } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export default function ProfesorAsignaturaPage() {
  const { asignaturaId } = useParams();
  const id = Number(asignaturaId);
  const { perfil } = useAuth();
  const [plantillas, setPlantillas] = useState<(PlantillaProyecto & { numGrupos?: number })[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const asignatura = (perfil?.asignaturasImpartidas ?? []).find((a) => a.id === id);

  useEffect(() => {
    if (!asignatura) return;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const list = await api.catalogo.plantillas(id);
        const conGrupos = await Promise.all(
          list.map(async (pl) => {
            try {
              const grupos = await api.profesor.gruposPorPlantilla(pl.id);
              return { ...pl, numGrupos: grupos.length };
            } catch {
              return { ...pl, numGrupos: 0 };
            }
          }),
        );
        setPlantillas(conGrupos);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar plantillas');
      } finally {
        setLoading(false);
      }
    })();
  }, [asignatura, id]);

  if (!perfil) return <p>Cargando…</p>;
  if (!asignatura || Number.isNaN(id)) {
    return <Navigate to="/profesor" replace />;
  }

  return (
    <div className="profesor-asignatura-page">
      <div className="page-header page-header-row">
        <div>
          <Link to="/profesor" className="muted profesor-back-link">← Portal del tutor</Link>
          <h2 style={{ margin: '0.35rem 0 0' }}>{asignatura.nombre}</h2>
          {asignatura.descripcion && <p className="muted">{asignatura.descripcion}</p>}
        </div>
        <Link to={`/profesor/asignaturas/${id}/plantillas/nueva`} className="btn">
          + Nueva plantilla
        </Link>
      </div>

      <div className="card profesor-intro-card">
        <p style={{ margin: 0 }}>
          Selecciona una <strong>plantilla de proyecto</strong> para ver su definición y los grupos de alumnos
          que han creado un proyecto a partir de ella.
        </p>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {loading && <p>Cargando plantillas…</p>}

      {!loading && plantillas.length === 0 && (
        <div className="card" style={{ textAlign: 'center', padding: '2rem' }}>
          <p className="muted" style={{ marginBottom: '1rem' }}>
            Todavía no has creado plantillas para esta asignatura.
          </p>
          <Link to={`/profesor/asignaturas/${id}/plantillas/nueva`} className="btn">
            Crear la primera plantilla
          </Link>
        </div>
      )}

      {!loading && plantillas.length > 0 && (
        <div className="profesor-plantillas-grid">
          {plantillas.map((pl, index) => (
            <Link
              key={pl.id}
              to={`/profesor/asignaturas/${id}/plantillas/${pl.id}`}
              className="card profesor-plantilla-card profesor-asignatura-card--link"
            >
              <span className="profesor-plantilla-orden">Proyecto {pl.orden ?? index + 1}</span>
              <h3 style={{ margin: '0.35rem 0 0.5rem' }}>{pl.titulo}</h3>
              {pl.descripcion && (
                <p className="muted" style={{ fontSize: '0.9rem', margin: '0 0 0.75rem' }}>
                  {pl.descripcion}
                </p>
              )}
              <div className="profesor-plantilla-chips">
                <span className="profesor-pill">{pl.numTareas} tareas</span>
                <span className="profesor-pill">{pl.numHitos} hitos</span>
                <span className="profesor-pill profesor-pill--accent">
                  {pl.numGrupos ?? 0} grupos
                </span>
              </div>
              <span className="profesor-asignatura-card-cta">Ver plantilla y grupos →</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
