import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { api, type Universidad } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { firebaseConfigured } from '../auth/firebase';
import { emailValidoParaUniversidad, mensajeEmailUniversidad } from '../utils/emailUniversidad';

export default function RegistroProfesorPage() {
  const { user, perfil, loading, perfilLoading, registerProfesor, refreshPerfil } = useAuth();
  const navigate = useNavigate();
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [codigoProfesor, setCodigoProfesor] = useState('');
  const [universidadId, setUniversidadId] = useState('');
  const [universidades, setUniversidades] = useState<Universidad[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const list = await api.universidades.list();
        setUniversidades(list);
        if (list.length > 0) setUniversidadId(String(list[0].id));
      } catch {
        setError('No se pudo cargar el listado de universidades');
      }
    })();
  }, []);

  const universidadSel = useMemo(
    () => universidades.find((u) => String(u.id) === universidadId),
    [universidades, universidadId],
  );

  if (!loading && user && !perfilLoading && perfil?.tipo === 'PROFESOR') {
    if (!perfil.matriculacionCompleta) {
      return <Navigate to="/profesor/asignaturas" replace />;
    }
    return <Navigate to="/profesor" replace />;
  }

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (!nombre.trim()) {
      setError('Indica tu nombre');
      return;
    }
    if (!codigoProfesor.trim()) {
      setError('Introduce el código de profesor de tu universidad');
      return;
    }
    if (!emailValidoParaUniversidad(email, universidadSel)) {
      setError(mensajeEmailUniversidad(universidadSel));
      return;
    }

    setBusy(true);
    try {
      if (user) {
        await api.usuarios.registroProfesor({
          firebaseUid: user.uid,
          email: email.trim(),
          nombre: nombre.trim(),
          universidadId: Number(universidadId),
          codigoProfesor: codigoProfesor.trim(),
        });
        await refreshPerfil();
        navigate('/profesor/asignaturas', { replace: true });
      } else {
        await registerProfesor(
          email,
          password,
          nombre.trim(),
          Number(universidadId),
          codigoProfesor.trim(),
        );
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al registrar profesor');
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
      <form className="card login-card matriculacion-card" onSubmit={submit}>
        <h2>Registro de profesor / tutor</h2>
        <p className="muted">
          Cuenta institucional verificada con Firebase y código de la universidad.
          Si ya te registraste como estudiante con este email, usa la misma contraseña.
          Podrás elegir tus asignaturas en el siguiente paso.
        </p>

        {error && <div className="alert alert-warn">{error}</div>}

        <div className="form-group">
          <label>Nombre</label>
          <input
            type="text"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            required
            autoComplete="name"
          />
        </div>

        <div className="form-group">
          <label>Email institucional</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          {universidadSel && <small className="muted">{mensajeEmailUniversidad(universidadSel)}</small>}
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

        <div className="form-group">
          <label>Universidad</label>
          <select
            value={universidadId}
            onChange={(e) => setUniversidadId(e.target.value)}
            required
          >
            {universidades.map((u) => (
              <option key={u.id} value={u.id}>{u.nombre}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Código de profesor</label>
          <input
            type="password"
            value={codigoProfesor}
            onChange={(e) => setCodigoProfesor(e.target.value)}
            required
            placeholder="Código proporcionado por la universidad"
          />
          
        </div>

        <button type="submit" className="btn" disabled={busy}>
          {busy ? 'Registrando…' : user ? 'Completar registro de profesor' : 'Crear cuenta de profesor'}
        </button>
        <Link to="/login" className="btn btn-secondary" style={{ display: 'block', textAlign: 'center', marginTop: '0.5rem' }}>
          Volver al inicio de sesión
        </Link>
      </form>
    </div>
  );
}
