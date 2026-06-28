const ETIQUETAS: Record<string, string> = {
  PRODUCT_OWNER: 'Product Owner',
  SCRUM_MASTER: 'Scrum Master',
  DEVELOPER: 'Equipo de Desarrollo',
  TUTOR: 'Tutor',
};

export function etiquetaRol(rol: string, rolEtiqueta?: string): string {
  return rolEtiqueta ?? ETIQUETAS[rol] ?? rol;
}

export const ROLES_INVITACION = ['DEVELOPER', 'SCRUM_MASTER'] as const;

export function miembrosAsignables<T extends { rol: string }>(miembros: T[]): T[] {
  return miembros.filter((m) => m.rol !== 'TUTOR');
}

export function esProductOwner(
  miembros: { usuarioUid: string; rol: string }[],
  usuarioUid?: string | null,
  propietarioUid?: string | null,
): boolean {
  if (!usuarioUid) return false;
  if (propietarioUid && propietarioUid === usuarioUid) return true;
  return miembros.some((m) => m.usuarioUid === usuarioUid && m.rol === 'PRODUCT_OWNER');
}
