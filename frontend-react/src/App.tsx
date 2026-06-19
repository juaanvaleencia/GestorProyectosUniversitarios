import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProyectosPage from './pages/ProyectosPage';
import ProyectoDetailPage from './pages/ProyectoDetailPage';
import ProyectoFormPage from './pages/ProyectoFormPage';
import InformesPage from './pages/InformesPage';
import NotificacionesPage from './pages/NotificacionesPage';
import PerfilPage from './pages/PerfilPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="proyectos" element={<ProyectosPage />} />
        <Route path="proyectos/nuevo" element={<ProyectoFormPage />} />
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
