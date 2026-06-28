export function etiquetaTutor(tutorNombre?: string | null): string {
  const nombre = tutorNombre?.trim();
  return nombre ? nombre : 'Sin tutor asignado';
}
