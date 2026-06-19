import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type Proyecto } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export default function ProyectosPage() {
  const { user } = useAuth();
  const [proyectos, setProyectos] = useState<Proyecto[]>([]);
  const [nombreUsuario, setNombreUsuario] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const perfil = await api.perfil().catch(() => null);
        const nombre =
          perfil?.nombre ?? user?.displayName ?? user?.email?.split('@')[0] ?? 'Usuario';
        setNombreUsuario(nombre);
        setProyectos(await api.proyectos.list());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar proyectos');
      } finally {
        setLoading(false);
      }
    })();
  }, [user]);

  return (
    <>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <h2>Proyectos</h2>
          <p>Listado de proyectos de {nombreUsuario}</p>
        </div>
        <Link to="/proyectos/nuevo" className="btn btn-secondary">
          Nuevo proyecto
        </Link>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}

      {loading ? (
        <p>Cargando…</p>
      ) : proyectos.length === 0 ? (
        <div className="card">
          <p className="muted" style={{ margin: 0 }}>
            No hay proyectos todavía. Pulsa «Nuevo proyecto» para crear el primero.
          </p>
        </div>
      ) : (
        <div className="grid grid-2">
          {proyectos.map((p) => (
            <div key={p.id} className="card">
              <h3 style={{ marginTop: 0 }}>
                <Link to={`/proyectos/${p.id}`}>{p.titulo}</Link>
              </h3>
              <p style={{ color: 'var(--muted)', fontSize: '0.9rem' }}>
                {p.descripcion?.slice(0, 120) ?? 'Sin descripción'}
              </p>
              <span className="badge badge-ok">{p.estado}</span>
              <div style={{ marginTop: '0.75rem' }}>
                <Link to={`/proyectos/${p.id}/editar`}>Editar proyecto</Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </>
  );
}
