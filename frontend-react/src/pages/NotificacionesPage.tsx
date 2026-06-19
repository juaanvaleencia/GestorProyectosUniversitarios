import { useEffect, useState } from 'react';
import { api, type Notificacion } from '../api/client';

export default function NotificacionesPage() {
  const [items, setItems] = useState<Notificacion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        setItems(await api.notificaciones.list());
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) return <p>Cargando…</p>;
  if (error) return <div className="alert alert-warn">{error}</div>;

  return (
    <>
      <div className="page-header">
        <h2>Notificaciones</h2>
        <p>Avisos asociados a tu usuario</p>
      </div>
      <div className="card">
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {items.map((n) => (
            <li
              key={n.id}
              style={{
                padding: '0.75rem 0',
                borderBottom: '1px solid var(--surface2)',
                opacity: n.leida ? 0.6 : 1,
              }}
            >
              {n.texto}
              <div style={{ color: 'var(--muted)', fontSize: '0.8rem' }}>{n.creadoEn}</div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
