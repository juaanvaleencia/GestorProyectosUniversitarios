import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type UsuarioPerfil } from '../api/client';
import { etiquetaRol } from '../utils/rolProyecto';

export default function PerfilPage() {
  const [perfil, setPerfil] = useState<UsuarioPerfil | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        setPerfil(await api.perfil());
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudo cargar el perfil');
      }
    })();
  }, []);

  if (error) return <div className="alert alert-warn">{error}</div>;
  if (!perfil) return <p>Cargando perfil…</p>;

  const iniciales = perfil.nombre
    .split(' ')
    .map((p) => p[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <>
      <div className="page-header">
        <h2>Mi perfil</h2>
      </div>

      <div className="card perfil-datos">
        <div className="perfil-avatar">{iniciales}</div>
        <div>
          <h3 style={{ margin: '0 0 0.25rem' }}>{perfil.nombre}</h3>
          <p className="muted" style={{ margin: 0 }}>
            {perfil.email}
          </p>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginTop: 0 }}>Proyectos en los que participo</h3>
        {perfil.participaciones.length === 0 ? (
          <p className="muted">No estás asignado a ningún proyecto todavía.</p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Proyecto</th>
                <th>Estado</th>
                <th>Mi rol</th>
              </tr>
            </thead>
            <tbody>
              {perfil.participaciones.map((p) => (
                <tr key={p.proyectoId}>
                  <td>
                    <Link to={`/proyectos/${p.proyectoId}`}>{p.titulo}</Link>
                  </td>
                  <td>{p.estado}</td>
                  <td>{etiquetaRol(p.rol, p.rolEtiqueta)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
