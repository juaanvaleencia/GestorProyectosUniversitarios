import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type Proyecto } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export default function DashboardPage() {
  const { user } = useAuth();
  const [proyectos, setProyectos] = useState<Proyecto[]>([]);
  const [error, setError] = useState<string | null>(null);
  const nombre = user?.displayName ?? user?.email?.split('@')[0] ?? 'estudiante';

  useEffect(() => {
    (async () => {
      try {
        setProyectos(await api.proyectos.list());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar proyectos');
      }
    })();
  }, []);

  return (
    <>
      <div className="page-header">
        <h1 className="app-title">Aplicación de Gestión de Proyectos universitarios</h1>
        <h2 style={{ marginTop: '0.75rem', fontSize: '1.25rem', fontWeight: 500 }}>
          Hola, {nombre}
        </h2>
      </div>

      {error && (
        <div className="alert alert-warn">
          {error}. Comprueba que el backend esté en marcha.
        </div>
      )}

      <div className="grid grid-2" style={{ marginBottom: '1.5rem' }}>
        <div className="card stat">
          <strong>{proyectos.length}</strong>
          <span>Proyectos</span>
        </div>
        <div className="card stat">
          <strong>{proyectos.filter((p) => p.estado === 'EN_CURSO').length}</strong>
          <span>En curso</span>
        </div>
      </div>

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ margin: 0 }}>Proyectos recientes</h3>
          <Link to="/proyectos/nuevo" className="btn btn-secondary">
            Nuevo proyecto
          </Link>
        </div>
        <ul style={{ marginTop: '1rem', paddingLeft: '1.2rem' }}>
          {proyectos.length === 0 && (
            <li className="muted">Aún no tienes proyectos. Crea uno con «Nuevo proyecto».</li>
          )}
          {proyectos.slice(0, 5).map((p) => (
            <li key={p.id}>
              <Link to={`/proyectos/${p.id}`}>{p.titulo}</Link>
              <span className="badge badge-ok" style={{ marginLeft: '0.5rem' }}>
                {p.estado}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
