import { createContext, useCallback, useContext, useEffect, useRef, useState, type ReactNode } from 'react';
import type { User } from 'firebase/auth';
import { api, setApiTokenGetter, type UsuarioPerfil } from '../api/client';
import { firebaseConfigured, loginEmail, logout, registerEmail, registerOrLoginEmail, subscribeAuth, updateUserProfile } from './firebase';

type AuthContextValue = {
  user: User | null;
  perfil: UsuarioPerfil | null;
  loading: boolean;
  perfilLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, nombre: string, universidadId: number) => Promise<void>;
  registerProfesor: (
    email: string,
    password: string,
    nombre: string,
    universidadId: number,
    codigoProfesor: string,
  ) => Promise<void>;
  asignarUniversidad: (universidadId: number) => Promise<void>;
  actualizarPerfil: (data: {
    nombre: string;
    avatarUrl?: string;
    universidadId?: number;
  }) => Promise<void>;
  refreshPerfil: () => Promise<void>;
  logout: () => Promise<void>;
  getToken: () => Promise<string | null>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

const SYNC_RETRIES = 8;
const SYNC_RETRY_MS = 800;

async function syncUsuarioWithRetry(
  u: User,
  options?: { universidadId?: number; nombre?: string; avatarUrl?: string },
): Promise<UsuarioPerfil | null> {
  const nombre =
    options?.nombre?.trim()
    || u.displayName?.trim()
    || (u.email?.includes('@') ? u.email.split('@')[0] : u.email)
    || 'Usuario';

  const body = {
    firebaseUid: u.uid,
    email: u.email ?? '',
    nombre,
    avatarUrl: options?.avatarUrl,
    universidadId: options?.universidadId,
  };

  for (let attempt = 0; attempt < SYNC_RETRIES; attempt++) {
    try {
      return await api.syncUsuario(body);
    } catch {
      if (attempt === SYNC_RETRIES - 1) return null;
      await new Promise((resolve) => setTimeout(resolve, SYNC_RETRY_MS));
    }
  }
  return null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [perfil, setPerfil] = useState<UsuarioPerfil | null>(null);
  const [loading, setLoading] = useState(true);
  const [perfilLoading, setPerfilLoading] = useState(false);
  const userRef = useRef<User | null>(null);
  const pendingUniversidadIdRef = useRef<number | null>(null);
  const pendingNombreRef = useRef<string | null>(null);
  const pendingProfesorRef = useRef<{
    nombre: string;
    universidadId: number;
    codigoProfesor: string;
  } | null>(null);

  useEffect(() => {
    setApiTokenGetter(async (forceRefresh = false) => {
      const current = userRef.current;
      if (!current) return null;
      return current.getIdToken(forceRefresh);
    });
  }, []);

  async function loadPerfilForUser(
    u: User,
    options?: { universidadId?: number; nombre?: string; avatarUrl?: string },
  ) {
    setPerfilLoading(true);
    try {
      await syncUsuarioWithRetry(u, options);
      setPerfil(await api.perfil());
    } catch {
      setPerfil(null);
    } finally {
      setPerfilLoading(false);
    }
  }

  async function fetchPerfil() {
    setPerfilLoading(true);
    try {
      setPerfil(await api.perfil());
    } catch {
      setPerfil(null);
    } finally {
      setPerfilLoading(false);
    }
  }

  const refreshPerfil = useCallback(async () => {
    const current = userRef.current;
    if (!current) return;
    await fetchPerfil();
  }, []);

  const actualizarPerfil = useCallback(async (data: {
    nombre: string;
    avatarUrl?: string;
    universidadId?: number;
  }) => {
    const current = userRef.current;
    if (!current) throw new Error('No hay sesión activa');

    const nombre = data.nombre.trim();
    if (!nombre) throw new Error('El nombre no puede estar vacío');

    const avatarUrl = data.avatarUrl?.trim() || undefined;

    await updateUserProfile(current, {
      displayName: nombre,
      photoURL: avatarUrl ?? null,
    });

    await syncUsuarioWithRetry(current, {
      nombre,
      avatarUrl,
      universidadId: data.universidadId,
    });
    setPerfil(await api.perfil());
  }, []);

  useEffect(() => {
    if (!firebaseConfigured) {
      setLoading(false);
      return;
    }

    return subscribeAuth((u) => {
      userRef.current = u;
      setUser(u);
      setLoading(false);

      if (u) {
        const pendingProfesor = pendingProfesorRef.current;
        if (pendingProfesor) {
          pendingProfesorRef.current = null;
          setPerfilLoading(true);
          (async () => {
            try {
              await api.usuarios.registroProfesor({
                firebaseUid: u.uid,
                email: u.email ?? '',
                nombre: pendingProfesor.nombre,
                universidadId: pendingProfesor.universidadId,
                codigoProfesor: pendingProfesor.codigoProfesor,
              });
              setPerfil(await api.perfil());
            } catch {
              setPerfil(null);
            } finally {
              setPerfilLoading(false);
            }
          })();
          return;
        }

        const universidadId = pendingUniversidadIdRef.current ?? undefined;
        const nombre = pendingNombreRef.current ?? undefined;
        pendingUniversidadIdRef.current = null;
        pendingNombreRef.current = null;
        void loadPerfilForUser(u, { universidadId, nombre });
      } else {
        setPerfil(null);
        setPerfilLoading(false);
      }
    });
  }, []);

  const value: AuthContextValue = {
    user,
    perfil,
    loading,
    perfilLoading,
    login: async (email, password) => {
      await loginEmail(email, password);
    },
    register: async (email, password, nombre, universidadId) => {
      pendingUniversidadIdRef.current = universidadId;
      pendingNombreRef.current = nombre.trim();
      await registerEmail(email, password, nombre);
    },
    registerProfesor: async (email, password, nombre, universidadId, codigoProfesor) => {
      pendingProfesorRef.current = {
        nombre: nombre.trim(),
        universidadId,
        codigoProfesor,
      };
      try {
        await registerOrLoginEmail(email, password, nombre);
      } catch (err) {
        pendingProfesorRef.current = null;
        throw err;
      }
    },
    asignarUniversidad: async (universidadId) => {
      const current = userRef.current;
      if (!current) throw new Error('No hay sesión activa');
      await loadPerfilForUser(current, { universidadId });
    },
    actualizarPerfil,
    refreshPerfil,
    logout: async () => {
      userRef.current = null;
      setUser(null);
      setPerfil(null);
      await logout();
    },
    getToken: async () => {
      const current = userRef.current;
      return current ? current.getIdToken() : null;
    },
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth dentro de AuthProvider');
  return ctx;
}
