import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import type { User } from 'firebase/auth';
import { api, setApiTokenGetter } from '../api/client';
import { firebaseConfigured, loginEmail, logout, registerEmail, subscribeAuth } from './firebase';

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  getToken: () => Promise<string | null>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setApiTokenGetter(async () => {
      if (!user) return null;
      return user.getIdToken();
    });
  }, [user]);

  useEffect(() => {
    if (!firebaseConfigured) {
      setLoading(false);
      return;
    }
    return subscribeAuth(async (u) => {
      setUser(u);
      setLoading(false);
      if (u) {
        try {
          await api.syncUsuario({
            firebaseUid: u.uid,
            email: u.email ?? '',
            nombre: u.displayName ?? u.email ?? 'Usuario',
          });
        } catch {
          /* backend puede no estar levantado aún */
        }
      }
    });
  }, []);

  const value: AuthContextValue = {
    user,
    loading,
    login: async (email, password) => {
      await loginEmail(email, password);
    },
    register: async (email, password) => {
      await registerEmail(email, password);
    },
    logout: async () => {
      setUser(null);
      await logout();
    },
    getToken: async () => (user ? user.getIdToken() : null),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth dentro de AuthProvider');
  return ctx;
}
