import { FormEvent, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { api, type Universidad } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export default function CompletarPerfilPage() {
  const { user, perfil, perfilLoading, asignarUniversidad } = useAuth();
  const [universidades, setUniversidades] = useState<Universidad[]>([]);
  const [universidadId, setUniversidadId] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const list = await api.universidades.list();
        setUniversidades(list);
        if (list.length > 0) {
          setUniversidadId(String(list[0].id));
        }
      } catch {
        setError('No se pudo cargar el listado de universidades');
      }
    })();
  }, []);

  if (!user) return <Navigate to="/login" replace />;
  if (!perfilLoading && perfil?.universidadId) return <Navigate to="/" replace />;

  async function submit(e: FormEvent) {
    e.preventDefault();
    if (!universidadId) {
      setError('Selecciona tu universidad');
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await asignarUniversidad(Number(universidadId));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar la universidad');
    } finally {
      setBusy(false);
    }
  }

  if (perfilLoading) return <p style={{ padding: '2rem' }}>Cargando…</p>;

  return (
    <div className="login-page">
      <form className="card login-card" onSubmit={submit}>
        <h2>Completa tu perfil</h2>
        <p className="muted">
          Indica a qué universidad perteneces para personalizar la aplicación.
        </p>

        {error && <div className="alert alert-warn">{error}</div>}

        <div className="form-group">
          <label>Universidad</label>
          <select
            value={universidadId}
            onChange={(e) => setUniversidadId(e.target.value)}
            required
          >
            {universidades.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nombre}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn" disabled={busy}>
          {busy ? 'Guardando…' : 'Continuar'}
        </button>
      </form>
    </div>
  );
}
