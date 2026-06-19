import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Layout() {
  const { user, logout } = useAuth();

  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>UPSA · Proyectos</h1>
        <NavLink to="/" end>Inicio</NavLink>
        <NavLink to="/proyectos">Proyectos</NavLink>
        <NavLink to="/informes">Informes</NavLink>
        <NavLink to="/notificaciones">Notificaciones</NavLink>
        <NavLink to="/perfil">Perfil</NavLink>
        <div style={{ flex: 1 }} />
        <small style={{ color: 'var(--muted)', marginBottom: '0.5rem', fontSize: '0.75rem' }}>
          TFG — prototipo (solo lectura en la mayoría de pantallas)
        </small>
        <small style={{ color: 'var(--muted)', marginBottom: '0.5rem' }}>
          {user?.email ?? '—'}
        </small>
        <button type="button" className="btn btn-secondary" onClick={() => logout()}>
          Cerrar sesión
        </button>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
