import { FormEvent, useState } from 'react';
import { api, type Asignatura } from '../api/client';

type Props = {
  onCreada?: (asignatura: Asignatura) => void;
};

export default function CrearAsignaturaForm({ onCreada }: Props) {
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!nombre.trim()) {
      setError('El nombre es obligatorio');
      return;
    }
    setBusy(true);
    setError(null);
    setOk(null);
    try {
      const creada = await api.usuarios.crearAsignatura({
        nombre: nombre.trim(),
        descripcion: descripcion.trim() || undefined,
      });
      setNombre('');
      setDescripcion('');
      setOk(`Asignatura «${creada.nombre}» creada y asignada como tutor`);
      onCreada?.(creada);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo crear la asignatura');
    } finally {
      setBusy(false);
    }
  }

  return (
    <form onSubmit={(e) => void handleSubmit(e)} className="crear-asignatura-form">
      <h4 style={{ marginTop: 0 }}>Crear nueva asignatura</h4>
      <p className="muted" style={{ fontSize: '0.9rem', marginTop: 0 }}>
        Al crearla quedarás asignado automáticamente como tutor.
      </p>
      {error && <div className="alert alert-warn">{error}</div>}
      {ok && <div className="alert alert-ok">{ok}</div>}
      <div className="form-group">
        <label htmlFor="asig-nombre">Nombre</label>
        <input
          id="asig-nombre"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          placeholder="Ej. Desarrollo de Aplicaciones Web"
          maxLength={200}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="asig-descripcion">Descripción (opcional)</label>
        <textarea
          id="asig-descripcion"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
          rows={2}
          maxLength={1000}
        />
      </div>
      <button type="submit" className="btn" disabled={busy}>
        {busy ? 'Creando…' : 'Crear asignatura'}
      </button>
    </form>
  );
}
