import { FormEvent, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { firebaseConfigured } from '../auth/firebase';

export default function LoginPage() {
  const { user, loading, login, register } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  if (!loading && user) return <Navigate to="/" replace />;

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      if (mode === 'login') await login(email, password);
      else await register(email, password);
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
        <h2>Planificación de proyectos</h2>
        <p className="muted">TFG — Universidad Pontificia de Salamanca</p>

        {error && <div className="alert alert-warn">{error}</div>}

        <div className="form-group">
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
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

        <button type="submit" className="btn" disabled={busy}>
          {mode === 'login' ? 'Iniciar sesión' : 'Registrarse'}
        </button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
        >
          {mode === 'login' ? 'Crear cuenta' : 'Ya tengo cuenta'}
        </button>
      </form>
    </div>
  );
}
