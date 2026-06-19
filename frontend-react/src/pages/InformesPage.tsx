import { useEffect, useState } from 'react';
import { api, type InformesResumen } from '../api/client';

export default function InformesPage() {
  const [data, setData] = useState<InformesResumen | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        setData(await api.informesResumen());
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Error al cargar informes');
      }
    })();
  }, []);

  if (error) return <div className="alert alert-warn">{error}</div>;
  if (!data) return <p>Cargando informes…</p>;

  return (
    <>
      <div className="page-header">
        <h2>Informes</h2>
        <p>{data.mensaje ?? 'Resumen de actividad'}</p>
      </div>

      <div className="grid grid-3">
        <div className="card stat">
          <strong>{data.proyectosActivos}</strong>
          <span>Proyectos activos</span>
        </div>
        <div className="card stat">
          <strong>{data.tareasCompletadas}</strong>
          <span>Tareas hechas</span>
        </div>
        <div className="card stat">
          <strong>{data.progresoMedio}%</strong>
          <span>Progreso medio</span>
        </div>
      </div>

      <div className="card">
        <h3>Actividad semanal</h3>
        {data.proyectosActivos === 0 && data.tareasCompletadas === 0 && data.tareasPendientes === 0 && (
          <p className="muted" style={{ marginTop: '0.5rem' }}>
            No hay tareas con fecha en la semana actual.
          </p>
        )}
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', height: 160, marginTop: '1rem' }}>
          {data.actividadSemanal.map((d) => (
            <div key={d.dia} style={{ flex: 1, textAlign: 'center' }}>
              <div
                style={{
                  height: `${Math.max(d.tareas * 18, 8)}px`,
                  background: 'var(--accent)',
                  borderRadius: 6,
                }}
              />
              <small>{d.dia}</small>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
