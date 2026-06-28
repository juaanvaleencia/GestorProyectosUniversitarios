import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { api, type Universidad } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { firebaseConfigured } from '../auth/firebase';
import { emailValidoParaUniversidad, mensajeEmailUniversidad } from '../utils/emailUniversidad';

export default function LoginPage() {
  const { user, perfil, loading, perfilLoading, login, register } = useAuth();
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [universidadId, setUniversidadId] = useState('');
  const [universidades, setUniversidades] = useState<Universidad[]>([]);
  const [mode, setMode] = useState<'login' | 'register'>('login');
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

  const universidadSel = useMemo(
    () => universidades.find((u) => String(u.id) === universidadId),
    [universidades, universidadId],
  );

  if (!loading && user && !perfilLoading && perfil) {
    if (perfil.tipo === 'PROFESOR') {
      if (!perfil.matriculacionCompleta) {
        return <Navigate to="/profesor/asignaturas" replace />;
      }
      return <Navigate to="/profesor" replace />;
    }
    if (!perfil.universidadId) return <Navigate to="/completar-perfil" replace />;
    if (!perfil.matriculacionCompleta) {
      return <Navigate to="/matriculacion" replace />;
    }
    return <Navigate to="/" replace />;
  }

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (mode === 'register' && !nombre.trim()) {
      setError('Indica tu nombre');
      return;
    }

    if (mode === 'register' && !universidadId) {
      setError('Selecciona tu universidad');
      return;
    }

    if (mode === 'register' && !emailValidoParaUniversidad(email, universidadSel)) {
      setError(mensajeEmailUniversidad(universidadSel));
      return;
    }

    setBusy(true);
    try {
      if (mode === 'login') await login(email, password);
      else await register(email, password, nombre.trim(), Number(universidadId));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error de autenticación');
    } finally {
      setBusy(false);
    }
  }

  if (!firebaseConfigured) {
    return (
      <div className="login-page">
        <div className="card login-card">
          <h2>Configuración requerida</h2>
          <p>Copia <code>.env.example</code> a <code>.env</code> con las claves de Firebase.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="login-page">
      <form className="card login-card" onSubmit={submit}>
        <h2>Gestor de planificación de proyectos</h2>
        <p className="muted">Proyectos universitarios — UPSA / USAL</p>

        {error && <div className="alert alert-warn">{error}</div>}

        {mode === 'register' && (
          <div className="form-group">
            <label>Nombre</label>
            <input
              type="text"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
              autoComplete="name"
              placeholder="Tu nombre y apellidos"
            />
          </div>
        )}

        <div className="form-group">
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          {mode === 'register' && universidadSel && (
            <small className="muted">{mensajeEmailUniversidad(universidadSel)}</small>
          )}
        </div>
        <div className="form-group">
          <label>Contraseña</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={6}
          />
        </div>

        {mode === 'register' && (
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
        )}

        <button type="submit" className="btn" disabled={busy}>
          {mode === 'login' ? 'Iniciar sesión' : 'Registrarse'}
        </button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
        >
          {mode === 'login' ? 'Crear cuenta de estudiante' : 'Ya tengo cuenta'}
        </button>
        {mode === 'login' && (
          <a href="/registro-profesor" className="btn btn-secondary" style={{ display: 'block', textAlign: 'center', marginTop: '0.5rem' }}>
            Soy profesor / tutor
          </a>
        )}
      </form>
    </div>
  );
}
