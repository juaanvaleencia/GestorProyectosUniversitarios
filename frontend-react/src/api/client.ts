const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export type Proyecto = {
  id: number;
  titulo: string;
  descripcion?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado: string;
  propietarioUid?: string;
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
};

export type TareaInput = {
  titulo: string;
  descripcion?: string;
  estado: string;
  prioridad: string;
  responsableUid?: string;
  fechaLimite?: string;
  orden?: number;
};

export type ProyectoInput = {
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

export type Miembro = {
  id: number;
  proyectoId: number;
  usuarioUid: string;
  email: string;
  nombre: string;
  rol: string;
  rolEtiqueta?: string;
};

export type ProyectoParticipacion = {
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
  participaciones: ProyectoParticipacion[];
};

export type Notificacion = {
  id: number;
  usuarioUid: string;
  texto: string;
  leida: boolean;
  creadoEn: string;
};

export type InformesResumen = {
  mensaje?: string;
  proyectosActivos: number;
  tareasCompletadas: number;
  tareasPendientes: number;
  progresoMedio: number;
  actividadSemanal: { dia: string; tareas: number }[];
};

type TokenGetter = () => Promise<string | null>;

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
  const token = skipAuth ? null : await tokenGetter();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(fetchOptions.headers as Record<string, string>),
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...fetchOptions, headers });
  if (!res.ok) {
    const errBody = await res.json().catch(() => null);
    throw new Error(parseErrorMessage(errBody, res.status));
  }
  if (res.status === 204 || res.status === 205) return undefined as T;
  const text = await res.text();
  if (!text) return undefined as T;
  return JSON.parse(text) as T;
}

export const api = {
  health: () =>
    request<{ status: string; service?: string }>('/api/health', { skipAuth: true }),

  proyectos: {
    list: () => request<Proyecto[]>('/api/proyectos'),
    create: (body: ProyectoInput) =>
      request<Proyecto>('/api/proyectos', {
        method: 'POST',
        body: JSON.stringify(body),
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
    get: (proyectoId: number, id: number) =>
      request<Tarea>(`/api/proyectos/${proyectoId}/tareas/${id}`),
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

  hitos: (proyectoId: number) => request<Hito[]>(`/api/proyectos/${proyectoId}/hitos`),

  miembros: (proyectoId: number) => request<Miembro[]>(`/api/proyectos/${proyectoId}/miembros`),

  syncUsuario: (body: { firebaseUid: string; email: string; nombre: string; avatarUrl?: string }) =>
    request<UsuarioPerfil>('/api/usuarios/sync', { method: 'POST', body: JSON.stringify(body) }),

  perfil: () => request<UsuarioPerfil>('/api/usuarios/perfil'),

  informesResumen: () => request<InformesResumen>('/api/informes/resumen'),

  notificaciones: {
    list: () => request<Notificacion[]>('/api/notificaciones'),
  },
};
