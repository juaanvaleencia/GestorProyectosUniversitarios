const ETIQUETAS: Record<string, string> = {
  PRODUCT_OWNER: 'Product Owner',
  SCRUM_MASTER: 'Scrum Master',
  DEVELOPER: 'Equipo de Desarrollo',
};

export function etiquetaRol(rol: string, rolEtiqueta?: string): string {
  return rolEtiqueta ?? ETIQUETAS[rol] ?? rol;
}
