import type { User } from 'firebase/auth';
import type { UsuarioPerfil } from '../api/client';

/** Nombre visible del usuario (perfil → Firebase → email). */
export function nombreParaMostrar(
  perfil: UsuarioPerfil | null | undefined,
  user: User | null | undefined,
): string {
  const desdePerfil = perfil?.nombre?.trim();
  if (desdePerfil) return desdePerfil;

  const desdeFirebase = user?.displayName?.trim();
  if (desdeFirebase) return desdeFirebase;

  const email = user?.email ?? perfil?.email;
  if (email?.includes('@')) return email.split('@')[0];

  return 'Usuario';
}
