import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import MatriculacionSelector from '../components/MatriculacionSelector';

export default function MatriculacionPage() {
  const { perfil, refreshPerfil } = useAuth();
  const navigate = useNavigate();

  if (!perfil) return <p style={{ padding: '2rem' }}>Cargando…</p>;

  if (perfil.tipo === 'PROFESOR') {
    return <Navigate to={perfil.matriculacionCompleta ? '/profesor' : '/profesor/asignaturas'} replace />;
  }

  if (perfil.matriculacionCompleta) {
    return <Navigate to="/" replace />;
  }

  const initialIds = (perfil.asignaturasMatriculadas ?? []).map((a) => a.id);

  return (
    <div className="login-page">
      <div className="card login-card matriculacion-card">
        <h2>Matrícula de asignaturas</h2>
        <p className="muted">
          Selecciona en qué asignaturas de {perfil.universidadNombre ?? 'tu universidad'} estás matriculado.
        </p>

        <MatriculacionSelector
          universidadId={perfil.universidadId}
          initialSeleccion={initialIds}
          submitLabel="Continuar"
          onGuardado={async () => {
            await refreshPerfil();
            navigate('/', { replace: true });
          }}
        />
      </div>
    </div>
  );
}
