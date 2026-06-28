import type { Universidad } from '../api/client';

/** Valida email institucional según universidad (UPSA / USAL). */
export function emailValidoParaUniversidad(email: string, universidad: Universidad | undefined): boolean {
  const e = email.trim();
  if (!universidad) return false;
  const codigo = universidad.codigo.toUpperCase();
  if (codigo === 'USAL') return /^.+@usal\.es$/i.test(e);
  if (codigo === 'UPSA') return /^.+@upsa\.es$/i.test(e);
  return true;
}

export function mensajeEmailUniversidad(universidad: Universidad | undefined): string {
  if (!universidad) return 'Selecciona tu universidad';
  const dominio = universidad.codigo.toUpperCase() === 'USAL' ? '@usal.es' : '@upsa.es';
  return `Usa un correo institucional ${dominio}`;
}
