import { FormEvent, useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { api } from '../api/client';

export default function ProyectoFormPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const isCreate = location.pathname.endsWith('/nuevo');
  const proyectoId = isCreate ? NaN : Number(id);
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [estado, setEstado] = useState('PLANIFICACION');
  const [loading, setLoading] = useState(!isCreate);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);

  async function removeProyecto() {
    if (isCreate || Number.isNaN(proyectoId)) return;
    if (!window.confirm('¿Eliminar este proyecto?')) return;
    setDeleting(true);
    setError(null);
    try {
      await api.proyectos.delete(proyectoId);
      navigate('/proyectos', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al eliminar');
    } finally {
      setDeleting(false);
    }
  }

  useEffect(() => {
    if (isCreate) return;
    if (!id || Number.isNaN(proyectoId)) {
      setLoading(false);
      return;
    }
    (async () => {
      try {
        const p = await api.proyectos.get(proyectoId);
        setTitulo(p.titulo);
        setDescripcion(p.descripcion ?? '');
        setFechaInicio(p.fechaInicio ?? '');
        setFechaFin(p.fechaFin ?? '');
        setEstado(p.estado);
        setError(null);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudo cargar el proyecto');
      } finally {
        setLoading(false);
      }
    })();
  }, [id, proyectoId, isCreate]);

  async function submit(e: FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setOk(null);
    const body = {
      titulo,
      descripcion,
      fechaInicio: fechaInicio || undefined,
      fechaFin: fechaFin || undefined,
      estado,
    };
    try {
      if (isCreate) {
        const created = await api.proyectos.create(body);
        navigate(`/proyectos/${created.id}`, { replace: true });
        return;
      } else {
        if (!id || Number.isNaN(proyectoId)) return;
        await api.proyectos.update(proyectoId, body);
        setOk('Proyecto actualizado correctamente');
        setTimeout(() => navigate(`/proyectos/${proyectoId}`), 800);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <p>Cargando…</p>;
  if (!isCreate && (!id || Number.isNaN(proyectoId))) {
    return <div className="alert alert-warn">Proyecto no válido.</div>;
  }

  return (
    <>
      <div className="page-header">
        <h2>{isCreate ? 'Nuevo proyecto' : 'Editar proyecto'}</h2>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {ok && <div className="alert alert-ok">{ok}</div>}

      <form className="card" style={{ maxWidth: 560 }} onSubmit={submit}>
        <div className="form-group">
          <label>Título</label>
          <input value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Descripción</label>
          <textarea rows={4} value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Fecha inicio</label>
          <input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Fecha fin</label>
          <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Estado</label>
          <select value={estado} onChange={(e) => setEstado(e.target.value)}>
            <option value="PLANIFICACION">Planificación</option>
            <option value="EN_CURSO">En curso</option>
            <option value="PAUSADO">Pausado</option>
            <option value="FINALIZADO">Finalizado</option>
          </select>
        </div>
        <button type="submit" className="btn" disabled={saving}>
          {saving ? 'Guardando…' : isCreate ? 'Crear proyecto' : 'Guardar'}
        </button>
        <Link
          to={isCreate ? '/proyectos' : `/proyectos/${proyectoId}`}
          className="btn btn-secondary"
          style={{ marginLeft: '0.5rem' }}
        >
          Cancelar
        </Link>
        {!isCreate && (
          <button
            type="button"
            className="btn btn-secondary"
            style={{ marginLeft: '0.5rem' }}
            disabled={deleting || saving}
            onClick={removeProyecto}
          >
            {deleting ? 'Eliminando…' : 'Eliminar proyecto'}
          </button>
        )}
      </form>
    </>
  );
}
