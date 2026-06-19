import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  if (loading) return <p style={{ padding: '2rem' }}>Cargando…</p>;
  if (!user) return <Navigate to="/login" replace />;
  return <>{children}</>;
}
