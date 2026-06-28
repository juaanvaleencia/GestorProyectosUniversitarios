import { Navigate, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useAuth } from '../auth/AuthContext';
import MatriculacionSelector from '../components/MatriculacionSelector';
import CrearAsignaturaForm from '../components/CrearAsignaturaForm';

export default function ProfesorAsignaturasPage() {
  const { perfil, refreshPerfil } = useAuth();
  const navigate = useNavigate();
  const [catalogoKey, setCatalogoKey] = useState(0);

  if (!perfil) return <p style={{ padding: '2rem' }}>Cargando…</p>;

  if (perfil.tipo !== 'PROFESOR') {
    return <Navigate to="/" replace />;
  }

  if (perfil.matriculacionCompleta) {
    return <Navigate to="/profesor" replace />;
  }

  const initialIds = (perfil.asignaturasImpartidas ?? []).map((a) => a.id);

  return (
    <div className="login-page">
      <div className="card login-card matriculacion-card">
        <h2>Asignaturas que imparto</h2>
        <p className="muted">
          Selecciona las asignaturas que impartes en {perfil.universidadNombre ?? 'tu universidad'}.
        </p>

        <CrearAsignaturaForm
          onCreada={async () => {
            await refreshPerfil();
            setCatalogoKey((k) => k + 1);
          }}
        />

        <hr style={{ margin: '1.5rem 0' }} />

        <MatriculacionSelector
          key={catalogoKey}
          modo="profesor"
          universidadId={perfil.universidadId}
          initialSeleccion={initialIds}
          submitLabel="Continuar"
          onGuardado={async () => {
            await refreshPerfil();
            navigate('/profesor', { replace: true });
          }}
        />
      </div>
    </div>
  );
}
