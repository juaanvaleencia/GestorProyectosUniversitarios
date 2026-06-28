import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type AlumnoMatriculado } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { nombreParaMostrar } from '../utils/nombreUsuario';

type AsignaturaResumen = {
  id: number;
  nombre: string;
  descripcion?: string;
  numPlantillas: number;
  numGrupos: number;
};

export default function ProfesorDashboardPage() {
  const { perfil, user } = useAuth();
  const asignaturas = perfil?.asignaturasImpartidas ?? [];
  const [resumenes, setResumenes] = useState<AsignaturaResumen[]>([]);
  const [alumnos, setAlumnos] = useState<AlumnoMatriculado[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const [alumnosData, ...asignaturaData] = await Promise.all([
          api.profesor.alumnosMatriculados().catch(() => [] as AlumnoMatriculado[]),
          ...asignaturas.map(async (a) => {
            const plantillas = await api.catalogo.plantillas(a.id);
            const gruposPorPlantilla = await Promise.all(
              plantillas.map((p) => api.profesor.gruposPorPlantilla(p.id).catch(() => [])),
            );
            const numGrupos = gruposPorPlantilla.reduce((acc, g) => acc + g.length, 0);
            return {
              id: a.id,
              nombre: a.nombre,
              descripcion: a.descripcion,
              numPlantillas: plantillas.length,
              numGrupos,
            };
          }),
        ]);
        setAlumnos(alumnosData);
        setResumenes(asignaturaData);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar el portal');
      } finally {
        setLoading(false);
      }
    })();
  }, [asignaturas]);

  const nombre = nombreParaMostrar(perfil, user);

  return (
    <div className="dashboard-page profesor-dashboard profesor-dashboard--centered">
      <header className="dashboard-hero profesor-hero profesor-hero--centered">
        <p className="dashboard-eyebrow">Portal del tutor</p>
        <h1 className="dashboard-title">Hola, {nombre}</h1>
        <p className="dashboard-subtitle">
          Gestiona tus asignaturas y supervisa el trabajo de tus alumnos
          {perfil?.universidadNombre && (
            <> · <span className="dashboard-uni">{perfil.universidadNombre}</span></>
          )}
        </p>
      </header>

      {error && <div className="alert alert-warn profesor-centered-block">{error}</div>}

      <section className="card profesor-panel profesor-centered-block">
        <div className="profesor-panel-header profesor-panel-header--centered">
          <h3>Mis asignaturas</h3>
          <p className="muted">Selecciona una asignatura para gestionar plantillas y ver los grupos.</p>
        </div>
        {asignaturas.length === 0 ? (
          <p className="muted" style={{ textAlign: 'center' }}>
            Configura tus asignaturas en{' '}
            <Link to="/profesor/perfil">tu perfil</Link>.
          </p>
        ) : (
          <div className="profesor-asignaturas-grid profesor-asignaturas-grid--centered">
            {resumenes.map((a) => (
              <Link
                key={a.id}
                to={`/profesor/asignaturas/${a.id}`}
                className="card profesor-asignatura-card profesor-asignatura-card--link"
              >
                <div className="profesor-asignatura-card-top">
                  <h4>{a.nombre}</h4>
                  <span className="profesor-pill">{loading ? '…' : `${a.numPlantillas} plantillas`}</span>
                </div>
                {a.descripcion && (
                  <p className="muted profesor-asignatura-desc">{a.descripcion}</p>
                )}
                <div className="profesor-asignatura-meta">
                  <span>{loading ? '…' : `${a.numGrupos} grupos de alumnos`}</span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </section>

      <section className="card profesor-panel profesor-centered-block">
        <div className="profesor-panel-header profesor-panel-header--centered">
          <h3>Alumnos matriculados</h3>
          <p className="muted">
            Estudiantes inscritos en tus asignaturas. Accede a su perfil para ver matrículas y proyectos.
          </p>
        </div>

        {loading && <p className="muted" style={{ textAlign: 'center' }}>Cargando alumnos…</p>}

        {!loading && alumnos.length === 0 && (
          <p className="muted" style={{ textAlign: 'center' }}>
            Todavía no hay alumnos matriculados en tus asignaturas.
          </p>
        )}

        {!loading && alumnos.length > 0 && (
          <div className="profesor-alumnos-table-wrap">
            <table className="table profesor-alumnos-table">
              <thead>
                <tr>
                  <th>Alumno</th>
                  <th>Asignaturas en común</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {alumnos.map((al) => (
                  <tr key={al.uid}>
                    <td>
                      <strong>{al.nombre}</strong>
                      <br />
                      <small className="muted">{al.email}</small>
                    </td>
                    <td>
                      <div className="profesor-alumno-asignaturas">
                        {al.asignaturas.map((a) => (
                          <span key={a.id} className="profesor-pill">{a.nombre}</span>
                        ))}
                      </div>
                    </td>
                    <td style={{ textAlign: 'right', whiteSpace: 'nowrap' }}>
                      <Link to={`/profesor/alumnos/${encodeURIComponent(al.uid)}`} className="btn btn-secondary">
                        Ver perfil
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
