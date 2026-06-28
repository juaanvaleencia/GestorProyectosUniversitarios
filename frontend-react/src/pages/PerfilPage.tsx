import { FormEvent, useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import MatriculacionSelector from '../components/MatriculacionSelector';
import CrearAsignaturaForm from '../components/CrearAsignaturaForm';
import { api, type Universidad } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import { etiquetaRol } from '../utils/rolProyecto';

type Tab = 'resumen' | 'personalizacion' | 'matriculas' | 'asignaturas';

function inicialesDe(nombre: string): string {
  return nombre
    .split(' ')
    .filter(Boolean)
    .map((p) => p[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();
}

function PerfilAvatar({ nombre, avatarUrl }: { nombre: string; avatarUrl?: string }) {
  const [imgError, setImgError] = useState(false);
  const url = avatarUrl?.trim();

  useEffect(() => {
    setImgError(false);
  }, [url]);

  if (url && !imgError) {
    return (
      <img
        key={url}
        src={url}
        alt=""
        className="perfil-avatar perfil-avatar-img"
        onError={() => setImgError(true)}
      />
    );
  }

  return <div className="perfil-avatar">{inicialesDe(nombre)}</div>;
}

export default function PerfilPage() {
  const { perfil, perfilLoading, refreshPerfil, actualizarPerfil } = useAuth();
  const [tab, setTab] = useState<Tab>('resumen');
  const [catalogoKey, setCatalogoKey] = useState(0);
  const [universidades, setUniversidades] = useState<Universidad[]>([]);
  const [nombre, setNombre] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [universidadId, setUniversidadId] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [uniLoading, setUniLoading] = useState(false);
  const tabAnterior = useRef<Tab>('resumen');

  useEffect(() => {
    if (!perfil && !perfilLoading) {
      void refreshPerfil();
    }
  }, [perfil, perfilLoading, refreshPerfil]);

  useEffect(() => {
    const entraEnPersonalizacion =
      tab === 'personalizacion' && tabAnterior.current !== 'personalizacion';

    if (entraEnPersonalizacion && perfil) {
      setNombre(perfil.nombre);
      setAvatarUrl(perfil.avatarUrl ?? '');
      setUniversidadId(perfil.universidadId ? String(perfil.universidadId) : '');
      setError(null);
      setOk(null);
    }

    tabAnterior.current = tab;
  }, [tab, perfil]);

  useEffect(() => {
    if (tab !== 'personalizacion' || universidades.length > 0) return;

    setUniLoading(true);
    api.universidades
      .list()
      .then(setUniversidades)
      .catch((e) => {
        setError(e instanceof Error ? e.message : 'No se pudo cargar universidades');
      })
      .finally(() => setUniLoading(false));
  }, [tab, universidades.length]);

  const hayCambios = useMemo(() => {
    if (!perfil) return false;
    const uniActual = perfil.universidadId ? String(perfil.universidadId) : '';
    return (
      nombre.trim() !== perfil.nombre
      || avatarUrl.trim() !== (perfil.avatarUrl ?? '')
      || universidadId !== uniActual
    );
  }, [perfil, nombre, avatarUrl, universidadId]);

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setOk(null);

    if (!nombre.trim()) {
      setError('Indica tu nombre');
      return;
    }
    if (!universidadId) {
      setError('Selecciona tu universidad');
      return;
    }

    setBusy(true);
    try {
      await actualizarPerfil({
        nombre: nombre.trim(),
        avatarUrl: avatarUrl.trim() || undefined,
        universidadId: Number(universidadId),
      });
      setOk('Perfil actualizado correctamente');
      setTab('resumen');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar el perfil');
    } finally {
      setBusy(false);
    }
  }

  if (perfilLoading && !perfil) return <p>Cargando perfil…</p>;
  if (!perfil) return <p>Cargando perfil…</p>;

  const participaciones = perfil.participaciones ?? [];

  return (
    <>
      <div className="page-header page-header-row">
        <div>
          <h2>Mi perfil</h2>
          <p className="muted">Consulta tu información y participación en proyectos.</p>
        </div>
        {tab === 'resumen' && (
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => setTab('personalizacion')}
          >
            Personalizar perfil
          </button>
        )}
      </div>

      <div className="page-tabs" role="tablist" aria-label="Secciones del perfil">
        <button
          type="button"
          role="tab"
          aria-selected={tab === 'resumen'}
          className={`page-tab${tab === 'resumen' ? ' active' : ''}`}
          onClick={() => setTab('resumen')}
        >
          Resumen
        </button>
        {perfil.tipo === 'PROFESOR' ? (
          <button
            type="button"
            role="tab"
            aria-selected={tab === 'asignaturas'}
            className={`page-tab${tab === 'asignaturas' ? ' active' : ''}`}
            onClick={() => setTab('asignaturas')}
          >
            Asignaturas
          </button>
        ) : (
        <button
          type="button"
          role="tab"
          aria-selected={tab === 'matriculas'}
          className={`page-tab${tab === 'matriculas' ? ' active' : ''}`}
          onClick={() => setTab('matriculas')}
        >
          Matrículas
        </button>
        )}
        <button
          type="button"
          role="tab"
          aria-selected={tab === 'personalizacion'}
          className={`page-tab${tab === 'personalizacion' ? ' active' : ''}`}
          onClick={() => setTab('personalizacion')}
        >
          Personalización
        </button>
      </div>

      {ok && tab === 'resumen' && <div className="alert alert-ok">{ok}</div>}

      {tab === 'resumen' && (
        <>
          <div className="card perfil-datos">
            <PerfilAvatar nombre={perfil.nombre} avatarUrl={perfil.avatarUrl} />
            <div>
              <h3 style={{ margin: '0 0 0.25rem' }}>{perfil.nombre}</h3>
              <p className="muted" style={{ margin: 0 }}>{perfil.email}</p>
              {perfil.universidadNombre && (
                <p className="muted" style={{ margin: '0.35rem 0 0', fontSize: '0.9rem' }}>
                  {perfil.universidadNombre}
                </p>
              )}
              {(perfil.asignaturasMatriculadas ?? []).length > 0 && perfil.tipo !== 'PROFESOR' && (
                <p className="muted" style={{ margin: '0.35rem 0 0', fontSize: '0.9rem' }}>
                  {(perfil.asignaturasMatriculadas ?? []).map((a) => a.nombre).join(' · ')}
                </p>
              )}
              {(perfil.asignaturasImpartidas ?? []).length > 0 && perfil.tipo === 'PROFESOR' && (
                <p className="muted" style={{ margin: '0.35rem 0 0', fontSize: '0.9rem' }}>
                  Imparte: {(perfil.asignaturasImpartidas ?? []).map((a) => a.nombre).join(' · ')}
                </p>
              )}
            </div>
          </div>

          <div className="card">
            <h3 style={{ marginTop: 0 }}>Proyectos en los que participo</h3>
            {participaciones.length === 0 ? (
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
                  {participaciones.map((p) => (
                    <tr key={p.proyectoId}>
                      <td>
                        <Link to={`/proyectos/${p.proyectoId}`}>{p.titulo}</Link>
                      </td>
                      <td>{etiquetaEstadoProyecto(p.estado)}</td>
                      <td>{etiquetaRol(p.rol, p.rolEtiqueta)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      {tab === 'personalizacion' && (
        <form className="card perfil-form" onSubmit={submit}>
          <div className="perfil-form-header">
            <PerfilAvatar nombre={nombre || perfil.nombre} avatarUrl={avatarUrl} />
            <div>
              <h3 style={{ margin: '0 0 0.25rem' }}>Editar datos</h3>
              <p className="muted" style={{ margin: 0 }}>
                Cambia tu nombre, imagen o universidad.
              </p>
            </div>
          </div>

          {error && <div className="alert alert-warn">{error}</div>}

          <div className="form-group">
            <label htmlFor="perfil-nombre">Nombre</label>
            <input
              id="perfil-nombre"
              type="text"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
              autoComplete="name"
            />
          </div>

          <div className="form-group">
            <label htmlFor="perfil-avatar">Imagen de perfil (URL)</label>
            <input
              id="perfil-avatar"
              type="url"
              value={avatarUrl}
              onChange={(e) => setAvatarUrl(e.target.value)}
              placeholder="https://ejemplo.com/mi-foto.jpg"
            />
            <small className="muted">
              Enlace a una imagen pública. Déjalo vacío para usar iniciales.
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="perfil-email">Email</label>
            <input id="perfil-email" type="email" value={perfil.email} disabled />
          </div>

          <div className="form-group">
            <label htmlFor="perfil-universidad">Universidad</label>
            <select
              id="perfil-universidad"
              value={universidadId}
              onChange={(e) => setUniversidadId(e.target.value)}
              required
              disabled={uniLoading}
            >
              <option value="" disabled>
                {uniLoading ? 'Cargando…' : 'Selecciona una universidad'}
              </option>
              {universidades.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nombre}
                </option>
              ))}
            </select>
          </div>

          <div className="perfil-form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setTab('resumen')}
            >
              Cancelar
            </button>
            <button type="submit" className="btn" disabled={busy || !hayCambios}>
              {busy ? 'Guardando…' : 'Guardar cambios'}
            </button>
          </div>
        </form>
      )}

      {tab === 'asignaturas' && perfil.tipo === 'PROFESOR' && (
        <div className="card perfil-form">
          <h3 style={{ marginTop: 0 }}>Asignaturas que imparto</h3>
          {ok && tab === 'asignaturas' && <div className="alert alert-ok">{ok}</div>}
          <CrearAsignaturaForm
            onCreada={async () => {
              await refreshPerfil();
              setCatalogoKey((k) => k + 1);
              setOk('Asignatura creada correctamente');
            }}
          />
          <hr style={{ margin: '1.5rem 0' }} />
          <MatriculacionSelector
            key={catalogoKey}
            modo="profesor"
            universidadId={perfil.universidadId}
            initialSeleccion={(perfil.asignaturasImpartidas ?? []).map((a) => a.id)}
            submitLabel="Guardar asignaturas"
            onGuardado={async () => {
              await refreshPerfil();
              setOk('Asignaturas actualizadas');
            }}
          />
        </div>
      )}

      {tab === 'matriculas' && perfil.tipo !== 'PROFESOR' && (
        <div className="card perfil-form">
          <h3 style={{ marginTop: 0 }}>Mis matrículas</h3>
          {ok && tab === 'matriculas' && <div className="alert alert-ok">{ok}</div>}
          <MatriculacionSelector
            universidadId={perfil.universidadId}
            initialSeleccion={(perfil.asignaturasMatriculadas ?? []).map((a) => a.id)}
            submitLabel="Guardar matrículas"
            onGuardado={async () => {
              await refreshPerfil();
              setOk('Matrículas actualizadas');
            }}
          />
        </div>
      )}
    </>
  );
}
