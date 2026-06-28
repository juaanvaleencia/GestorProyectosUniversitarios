import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import LayoutProfesor from './components/LayoutProfesor';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProyectosPage from './pages/ProyectosPage';
import ProyectoDetailPage from './pages/ProyectoDetailPage';
import ProyectoFormPage from './pages/ProyectoFormPage';
import InformesPage from './pages/InformesPage';
import NotificacionesPage from './pages/NotificacionesPage';
import CompletarPerfilPage from './pages/CompletarPerfilPage';
import ProyectoNuevoPage from './pages/ProyectoNuevoPage';
import MatriculacionPage from './pages/MatriculacionPage';
import RegistroProfesorPage from './pages/RegistroProfesorPage';
import ProfesorDashboardPage from './pages/ProfesorDashboardPage';
import ProfesorAsignaturasPage from './pages/ProfesorAsignaturasPage';
import ProfesorAsignaturaPage from './pages/ProfesorAsignaturaPage';
import ProfesorPlantillaDetailPage from './pages/ProfesorPlantillaDetailPage';
import ProfesorPlantillaFormPage from './pages/ProfesorPlantillaFormPage';
import ProfesorAlumnoPerfilPage from './pages/ProfesorAlumnoPerfilPage';
import PerfilPage from './pages/PerfilPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro-profesor" element={<RegistroProfesorPage />} />
      <Route
        path="/completar-perfil"
        element={
          <ProtectedRoute>
            <CompletarPerfilPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/matriculacion"
        element={
          <ProtectedRoute role="ESTUDIANTE">
            <MatriculacionPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profesor/asignaturas"
        element={
          <ProtectedRoute role="PROFESOR">
            <ProfesorAsignaturasPage />
          </ProtectedRoute>
        }
      />
      <Route
        element={
          <ProtectedRoute role="PROFESOR">
            <LayoutProfesor />
          </ProtectedRoute>
        }
      >
        <Route path="/profesor" element={<ProfesorDashboardPage />} />
        <Route path="/profesor/asignaturas/:asignaturaId" element={<ProfesorAsignaturaPage />} />
        <Route path="/profesor/asignaturas/:asignaturaId/plantillas/nueva" element={<ProfesorPlantillaFormPage />} />
        <Route path="/profesor/asignaturas/:asignaturaId/plantillas/:plantillaId/editar" element={<ProfesorPlantillaFormPage />} />
        <Route path="/profesor/asignaturas/:asignaturaId/plantillas/:plantillaId" element={<ProfesorPlantillaDetailPage />} />
        <Route path="/profesor/proyectos/:id" element={<ProyectoDetailPage />} />
        <Route path="/profesor/alumnos/:uid" element={<ProfesorAlumnoPerfilPage />} />
        <Route path="/profesor/perfil" element={<PerfilPage />} />
      </Route>
      <Route
        element={
          <ProtectedRoute role="ESTUDIANTE">
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="proyectos" element={<ProyectosPage />} />
        <Route path="proyectos/nuevo" element={<ProyectoNuevoPage />} />
        <Route path="proyectos/:id/editar" element={<ProyectoFormPage />} />
        <Route path="proyectos/:id" element={<ProyectoDetailPage />} />
        <Route path="informes" element={<InformesPage />} />
        <Route path="notificaciones" element={<NotificacionesPage />} />
        <Route path="perfil" element={<PerfilPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
