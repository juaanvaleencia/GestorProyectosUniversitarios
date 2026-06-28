const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export type Proyecto = {
  id: number;
  titulo: string;
  descripcion?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado: string;
  propietarioUid?: string;
  asignaturaId?: number;
  asignaturaNombre?: string;
  creadoEn?: string;
  actualizadoEn?: string;
};

export type Tarea = {
  id: number;
  proyectoId: number;
  titulo: string;
  descripcion?: string;
  estado: string;
  prioridad: string;
  responsableUid?: string;
  fechaLimite?: string;
  orden: number;
  origen?: string;
  tareaPadreId?: number;
  letraSubtarea?: string;
};

export type TareaInput = {
  titulo: string;
  descripcion?: string;
  estado: string;
  prioridad: string;
  responsableUid?: string;
  fechaLimite?: string;
  orden?: number;
  tareaPadreId?: number;
};

type ProyectoInput = {
  titulo: string;
  descripcion?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado: string;
};

export type Hito = {
  id: number;
  proyectoId: number;
  titulo: string;
  fecha: string;
  completado: boolean;
};

export type HitoInput = {
  titulo: string;
  fecha: string;
  completado: boolean;
};

export type Miembro = {
  id: number;
  proyectoId: number;
  usuarioUid: string;
  email: string;
  nombre: string;
  rol: string;
  rolEtiqueta?: string;
};

type MiembroInviteInput = {
  email: string;
  rol: string;
};

export type InvitacionProyecto = {
  id: number;
  proyectoId: number;
  usuarioUid: string;
  email: string;
  nombre: string;
  rol: string;
  rolEtiqueta?: string;
  estado: string;
  invitadoPorNombre?: string;
  creadoEn?: string;
};

type ProyectoParticipacion = {
  proyectoId: number;
  titulo: string;
  estado: string;
  rol: string;
  rolEtiqueta?: string;
};

export type UsuarioPerfil = {
  nombre: string;
  email: string;
  avatarUrl?: string;
  universidadId?: number;
  universidadNombre?: string;
  tipo?: 'ESTUDIANTE' | 'PROFESOR';
  matriculacionCompleta?: boolean;
  asignaturasMatriculadas?: Asignatura[];
  asignaturasImpartidas?: Asignatura[];
  participaciones: ProyectoParticipacion[];
};

export type Universidad = {
  id: number;
  codigo: string;
  nombre: string;
};

export type Asignatura = {
  id: number;
  universidadId: number;
  nombre: string;
  descripcion?: string;
  tutorNombre?: string;
};

export type PlantillaProyecto = {
  id: number;
  asignaturaId: number;
  titulo: string;
  descripcion?: string;
  orden: number;
  fechaInicioSugerida?: string;
  fechaFinSugerida?: string;
  tutorNombre?: string;
  numTareas: number;
  numHitos: number;
};

type PlantillaTarea = {
  id: number;
  titulo: string;
  descripcion?: string;
  orden: number;
  fechaLimiteSugerida?: string;
};

type PlantillaHito = {
  id: number;
  titulo: string;
  fechaSugerida: string;
  orden: number;
};

export type PlantillaProyectoDetalle = PlantillaProyecto & {
  asignaturaNombre: string;
  tareas: PlantillaTarea[];
  hitos: PlantillaHito[];
};

export type PlantillaProyectoInput = {
  titulo: string;
  descripcion?: string;
  orden?: number;
  fechaInicioSugerida?: string;
  fechaFinSugerida?: string;
  tareas?: { titulo: string; descripcion?: string; orden?: number; fechaLimiteSugerida?: string }[];
  hitos?: { titulo: string; fechaSugerida: string; orden?: number }[];
};

type ParticipanteGrupo = {
  uid?: string;
  nombre: string;
  email: string;
  rol: string;
  rolEtiqueta?: string;
  propietario: boolean;
};

export type ProyectoGrupo = {
  id: number;
  titulo: string;
  estado: string;
  fechaInicio?: string;
  fechaFin?: string;
  actualizadoEn?: string;
  participantes: ParticipanteGrupo[];
};

export type ProfesorNavState = {
  asignaturaId?: number;
  asignaturaNombre?: string;
  plantillaId?: number;
  plantillaTitulo?: string;
};

export type AlumnoMatriculado = {
  uid: string;
  nombre: string;
  email: string;
  asignaturas: Asignatura[];
};

export type AlumnoPerfilSupervision = {
  uid: string;
  nombre: string;
  email: string;
  avatarUrl?: string;
  universidadNombre?: string;
  asignaturasMatriculadas: Asignatura[];
  participaciones: ProyectoParticipacion[];
};

export type Notificacion = {
  id: number;
  usuarioUid: string;
  texto: string;
  leida: boolean;
  creadoEn: string;
  tipo?: string;
  invitacionId?: number;
  proyectoId?: number;
  invitacionEstado?: string;
  invitacionSituacion?: 'ACCIONABLE' | 'ACEPTADA' | 'RECHAZADA' | 'OTRO_GRUPO' | 'YA_MIEMBRO';
};

export type InformesResumen = {
  mensaje?: string;
  proyectosActivos: number;
  tareasCompletadas: number;
  tareasPendientes: number;
  hitosCompletados: number;
  hitosPendientes: number;
  progresoMedio: number;
  actividadSemanal: { dia: string; fecha: string; tareas: number }[];
  tareasPendientesDetalle?: InformeTareaPendiente[];
  hitosPendientesDetalle?: InformeHitoPendiente[];
};

export type InformeTareaPendiente = {
  id: number;
  titulo: string;
  estado: string;
  fechaLimite?: string;
  proyectoId: number;
  proyectoTitulo: string;
};

export type InformeHitoPendiente = {
  id: number;
  titulo: string;
  fecha: string;
  proyectoId: number;
  proyectoTitulo: string;
};

type TokenGetter = (forceRefresh?: boolean) => Promise<string | null>;

let tokenGetter: TokenGetter = async () => null;

export function setApiTokenGetter(getter: TokenGetter) {
  tokenGetter = getter;
}

function parseErrorMessage(body: unknown, status: number): string {
  if (Array.isArray(body)) {
    return body
      .map((item) => {
        if (item && typeof item === 'object' && 'message' in item) {
          return String((item as { message: string }).message);
        }
        return String(item);
      })
      .join(', ');
  }
  if (body && typeof body === 'object' && 'message' in body) {
    const message = String((body as { message: string }).message).trim();
    if (message) return message;
  }
  return `Error ${status}`;
}

async function request<T>(
  path: string,
  options: RequestInit & { skipAuth?: boolean } = {}
): Promise<T> {
  const { skipAuth, ...fetchOptions } = options;

  async function doFetch(forceRefresh: boolean): Promise<Response> {
    let token: string | null = null;
    if (!skipAuth) {
      token = await tokenGetter(forceRefresh);
    }
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(fetchOptions.headers as Record<string, string>),
    };
    if (token) headers.Authorization = `Bearer ${token}`;
    return fetch(`${API_BASE}${path}`, { ...fetchOptions, headers });
  }

  let res = await doFetch(false);
  if (res.status === 401 && !skipAuth) {
    res = await doFetch(true);
  }

  if (!res.ok) {
    const errBody = await res.json().catch(() => null);
    const message = parseErrorMessage(errBody, res.status);
    if (res.status === 404 && message === 'Error 404') {
      throw new Error('Recurso no encontrado. Comprueba que el backend está actualizado (./start-services.sh).');
    }
    throw new Error(message);
  }
  if (res.status === 204 || res.status === 205) return undefined as T;
  const text = await res.text();
  if (!text) return undefined as T;
  return JSON.parse(text) as T;
}

export const api = {
  proyectos: {
    list: () => request<Proyecto[]>('/api/proyectos'),
    create: (body: ProyectoInput) =>
      request<Proyecto>('/api/proyectos', {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    createFromPlantilla: (plantillaId: number) =>
      request<Proyecto>(`/api/proyectos/desde-plantilla/${plantillaId}`, {
        method: 'POST',
      }),
    get: (id: number) => request<Proyecto>(`/api/proyectos/${id}`),
    update: (id: number, body: ProyectoInput) =>
      request<void>(`/api/proyectos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(body),
      }),
    delete: (id: number) =>
      request<void>(`/api/proyectos/${id}`, {
        method: 'DELETE',
      }),
  },

  tareas: {
    list: (proyectoId: number) => request<Tarea[]>(`/api/proyectos/${proyectoId}/tareas`),
    create: (proyectoId: number, body: TareaInput) =>
      request<Tarea>(`/api/proyectos/${proyectoId}/tareas`, {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    update: (proyectoId: number, id: number, body: TareaInput) =>
      request<void>(`/api/proyectos/${proyectoId}/tareas/${id}`, {
        method: 'PUT',
        body: JSON.stringify(body),
      }),
    delete: (proyectoId: number, id: number) =>
      request<void>(`/api/proyectos/${proyectoId}/tareas/${id}`, {
        method: 'DELETE',
      }),
  },

  hitos: {
    list: (proyectoId: number) => request<Hito[]>(`/api/proyectos/${proyectoId}/hitos`),
    create: (proyectoId: number, body: HitoInput) =>
      request<Hito>(`/api/proyectos/${proyectoId}/hitos`, {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    update: (proyectoId: number, id: number, body: HitoInput) =>
      request<void>(`/api/proyectos/${proyectoId}/hitos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(body),
      }),
    delete: (proyectoId: number, id: number) =>
      request<void>(`/api/proyectos/${proyectoId}/hitos/${id}`, {
        method: 'DELETE',
      }),
  },

  miembros: {
    list: (proyectoId: number) => request<Miembro[]>(`/api/proyectos/${proyectoId}/miembros`),
    invite: (proyectoId: number, body: MiembroInviteInput) =>
      request<InvitacionProyecto>(`/api/proyectos/${proyectoId}/miembros`, {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    remove: (proyectoId: number, miembroId: number) =>
      request<void>(`/api/proyectos/${proyectoId}/miembros/${miembroId}`, {
        method: 'DELETE',
      }),
    abandonar: (proyectoId: number) =>
      request<void>(`/api/proyectos/${proyectoId}/miembros/abandonar`, { method: 'POST' }),
  },

  invitaciones: {
    list: (proyectoId: number) =>
      request<InvitacionProyecto[]>(`/api/proyectos/${proyectoId}/invitaciones`),
    accept: (invitacionId: number) =>
      request<void>(`/api/invitaciones/${invitacionId}/aceptar`, { method: 'POST' }),
    reject: (invitacionId: number) =>
      request<void>(`/api/invitaciones/${invitacionId}/rechazar`, { method: 'POST' }),
  },

  syncUsuario: (body: {
    firebaseUid: string;
    email: string;
    nombre: string;
    avatarUrl?: string;
    universidadId?: number;
  }) => request<UsuarioPerfil>('/api/usuarios/sync', { method: 'POST', body: JSON.stringify(body) }),

  perfil: () => request<UsuarioPerfil>('/api/usuarios/perfil'),

  usuarios: {
    updateMisAsignaturas: (asignaturaIds: number[]) =>
      request<Asignatura[]>('/api/usuarios/mis-asignaturas', {
        method: 'PUT',
        body: JSON.stringify({ asignaturaIds }),
      }),
    crearAsignatura: (body: { nombre: string; descripcion?: string }) =>
      request<Asignatura>('/api/usuarios/asignaturas', {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    registroProfesor: (body: {
      firebaseUid: string;
      email: string;
      nombre: string;
      universidadId: number;
      codigoProfesor: string;
    }) =>
      request<UsuarioPerfil>('/api/usuarios/registro-profesor', {
        method: 'POST',
        body: JSON.stringify(body),
      }),
  },

  universidades: {
    list: () =>
      request<Universidad[]>('/api/universidades', { skipAuth: true }),
  },

  catalogo: {
    asignaturas: (universidadId: number) =>
      request<Asignatura[]>(`/api/universidades/${universidadId}/asignaturas`),
    plantillas: (asignaturaId: number) =>
      request<PlantillaProyecto[]>(`/api/asignaturas/${asignaturaId}/plantillas`),
    createPlantilla: (asignaturaId: number, body: PlantillaProyectoInput) =>
      request<PlantillaProyectoDetalle>(`/api/asignaturas/${asignaturaId}/plantillas`, {
        method: 'POST',
        body: JSON.stringify(body),
      }),
    updatePlantilla: (plantillaId: number, body: PlantillaProyectoInput) =>
      request<PlantillaProyectoDetalle>(`/api/plantillas/${plantillaId}`, {
        method: 'PUT',
        body: JSON.stringify(body),
      }),
    plantilla: (plantillaId: number) =>
      request<PlantillaProyectoDetalle>(`/api/plantillas/${plantillaId}`),
  },

  profesor: {
    gruposPorPlantilla: (plantillaId: number) =>
      request<ProyectoGrupo[]>(`/api/profesor/plantillas/${plantillaId}/grupos`),
    alumnosMatriculados: () =>
      request<AlumnoMatriculado[]>('/api/profesor/alumnos'),
    alumnoPerfil: (uid: string) =>
      request<AlumnoPerfilSupervision>(`/api/profesor/alumnos/${encodeURIComponent(uid)}`),
  },

  informesResumen: () => request<InformesResumen>('/api/informes/resumen'),

  notificaciones: {
    list: () => request<Notificacion[]>('/api/notificaciones'),
  },
};
