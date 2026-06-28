import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api, type Miembro } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import Breadcrumbs from '../components/Breadcrumbs';
import { ESTADOS_PROYECTO, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { esProductOwner } from '../utils/rolProyecto';

export default function ProyectoFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { refreshPerfil, user } = useAuth();
  const proyectoId = Number(id);
  const [miembros, setMiembros] = useState<Miembro[]>([]);
  const [propietarioUid, setPropietarioUid] = useState<string | undefined>();
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [estado, setEstado] = useState('PLANIFICACION');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [tituloCargado, setTituloCargado] = useState('');

  async function removeProyecto() {
    if (Number.isNaN(proyectoId)) return;
    if (!window.confirm('¿Eliminar este proyecto?')) return;
    setDeleting(true);
    setError(null);
    try {
      await api.proyectos.delete(proyectoId);
      await refreshPerfil();
      navigate('/proyectos', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al eliminar');
    } finally {
      setDeleting(false);
    }
  }

  useEffect(() => {
    if (!id || Number.isNaN(proyectoId)) {
      setLoading(false);
      return;
    }
    (async () => {
      try {
        const p = await api.proyectos.get(proyectoId);
        const equipo = await api.miembros.list(proyectoId);
        setMiembros(equipo);
        setPropietarioUid(p.propietarioUid);
        if (!esProductOwner(equipo, user?.uid, p.propietarioUid)) {
          setError('Solo el Product Owner puede editar este proyecto');
          return;
        }
        setTitulo(p.titulo);
        setTituloCargado(p.titulo);
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
  }, [id, proyectoId, user?.uid]);

  const puedeEditar = esProductOwner(miembros, user?.uid, propietarioUid);

  async function submit(e: FormEvent) {
    e.preventDefault();
    if (!id || Number.isNaN(proyectoId)) return;
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
      await api.proyectos.update(proyectoId, body);
      setOk('Proyecto actualizado correctamente');
      setTimeout(() => navigate(`/proyectos/${proyectoId}`), 800);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <p>Cargando…</p>;
  if (!id || Number.isNaN(proyectoId)) {
    return <div className="alert alert-warn">Proyecto no válido.</div>;
  }

  const breadcrumbItems = [
    { label: 'Inicio', to: '/' },
    { label: 'Proyectos', to: '/proyectos' },
    { label: tituloCargado || 'Proyecto', to: `/proyectos/${proyectoId}` },
    { label: 'Editar' },
  ];

  return (
    <>
      <Breadcrumbs items={breadcrumbItems} />

      <div className="page-header">
        <h2>Editar proyecto</h2>
        <p className="muted">Modifica los datos del proyecto y guarda los cambios.</p>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}
      {ok && <div className="alert alert-ok">{ok}</div>}

      <form className="card" style={{ maxWidth: 560 }} onSubmit={submit}>
        <fieldset disabled={!puedeEditar || saving || deleting} style={{ border: 0, margin: 0, padding: 0 }}>
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
            {ESTADOS_PROYECTO.map((value) => (
              <option key={value} value={value}>
                {etiquetaEstadoProyecto(value)}
              </option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn" disabled={saving}>
          {saving ? 'Guardando…' : 'Guardar'}
        </button>
        <Link
          to={`/proyectos/${proyectoId}`}
          className="btn btn-secondary"
          style={{ marginLeft: '0.5rem' }}
        >
          Cancelar
        </Link>
        <button
            type="button"
            className="btn btn-danger"
            style={{ marginLeft: '0.5rem' }}
            disabled={deleting || saving}
            onClick={removeProyecto}
          >
            {deleting ? 'Eliminando…' : 'Eliminar proyecto'}
        </button>
        </fieldset>
      </form>
    </>
  );
}
