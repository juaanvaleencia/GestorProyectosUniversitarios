import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

type Role = 'ESTUDIANTE' | 'PROFESOR';

function homePath(perfil: { tipo?: string; matriculacionCompleta?: boolean } | null): string {
  if (perfil?.tipo === 'PROFESOR') {
    return perfil.matriculacionCompleta ? '/profesor' : '/profesor/asignaturas';
  }
  return '/';
}

export default function ProtectedRoute({
  children,
  role,
}: {
  children: React.ReactNode;
  role?: Role;
}) {
  const { user, perfil, loading, perfilLoading } = useAuth();
  const location = useLocation();

  if (loading || (user && perfilLoading && !perfil)) {
    return <p style={{ padding: '2rem' }}>Cargando…</p>;
  }
  if (!user) return <Navigate to="/login" replace />;

  const esProfesor = perfil?.tipo === 'PROFESOR';
  const esEstudiante = !perfil?.tipo || perfil.tipo === 'ESTUDIANTE';
  const enCompletarPerfil = location.pathname === '/completar-perfil';
  const enMatriculacion = location.pathname === '/matriculacion';
  const enProfesorAsignaturas = location.pathname === '/profesor/asignaturas';
  const faltaUniversidad = perfil && !perfil.universidadId;
  const faltaMatricula =
    esEstudiante && perfil?.universidadId && !perfil.matriculacionCompleta;
  const faltaAsignaturasProfesor =
    esProfesor && perfil?.universidadId && !perfil.matriculacionCompleta;

  if (role === 'PROFESOR' && esEstudiante) {
    return <Navigate to="/" replace />;
  }
  if (role === 'ESTUDIANTE' && esProfesor) {
    return <Navigate to={homePath(perfil)} replace />;
  }

  if (faltaUniversidad && !enCompletarPerfil && esEstudiante) {
    return <Navigate to="/completar-perfil" replace />;
  }
  if (!faltaUniversidad && enCompletarPerfil) {
    return <Navigate to={faltaMatricula ? '/matriculacion' : homePath(perfil)} replace />;
  }
  if (faltaMatricula && !enMatriculacion && !enCompletarPerfil && esEstudiante) {
    return <Navigate to="/matriculacion" replace />;
  }
  if (!faltaMatricula && enMatriculacion) {
    return <Navigate to={homePath(perfil)} replace />;
  }
  if (faltaAsignaturasProfesor && !enProfesorAsignaturas && esProfesor) {
    return <Navigate to="/profesor/asignaturas" replace />;
  }
  if (!faltaAsignaturasProfesor && enProfesorAsignaturas) {
    return <Navigate to="/profesor" replace />;
  }

  return <>{children}</>;
}
