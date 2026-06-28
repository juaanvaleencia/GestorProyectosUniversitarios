import { useEffect, useState, useCallback, useMemo, type DragEvent } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { api, type Hito, type HitoInput, type InvitacionProyecto, type Miembro, type ProfesorNavState, type Proyecto, type Tarea, type TareaInput } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import Breadcrumbs from '../components/Breadcrumbs';
import GanttChart from '../components/GanttChart';
import HitoFormModal from '../components/HitoFormModal';
import TareaFormModal from '../components/TareaFormModal';
import { claseBadgeEstadoProyecto, etiquetaEstadoProyecto } from '../utils/estadoProyecto';
import {
  ESTADOS_TAREA,
  etiquetaEstadoTarea,
  etiquetaPrioridadTarea,
} from '../utils/estadoTarea';
import { etiquetaRol, esProductOwner, miembrosAsignables, ROLES_INVITACION } from '../utils/rolProyecto';
import { hitoFueraDeRango, progresoHitos } from '../utils/hitoValidation';

type Tab = 'tareas' | 'cronograma' | 'equipo';

function formatDate(value?: string) {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleDateString('es-ES');
  } catch {
    return value;
  }
}

function toInput(t: Tarea): TareaInput {
  return {
    titulo: t.titulo,
    descripcion: t.descripcion,
    estado: t.estado,
    prioridad: t.prioridad,
    responsableUid: t.responsableUid,
    fechaLimite: t.fechaLimite,
    orden: t.orden,
  };
}

function toHitoInput(h: Hito): HitoInput {
  return {
    titulo: h.titulo,
    fecha: h.fecha,
    completado: h.completado,
  };
}

type KanbanBlock =
  | { type: 'profesor-group'; parent: Tarea; subtareasEnColumna: Tarea[] }
  | { type: 'root'; tarea: Tarea }
  | { type: 'subtarea-suelta'; tarea: Tarea; parent: Tarea };

function normEstado(estado: string) {
  return estado.trim().toUpperCase().replace(/-/g, '_');
}

function subtareasDePadre(parentId: number, tareas: Tarea[]) {
  return tareas
    .filter((t) => t.tareaPadreId === parentId)
    .sort((a, b) => (a.letraSubtarea ?? '').localeCompare(b.letraSubtarea ?? ''));
}

function progresoSubtareas(parentId: number, tareas: Tarea[]) {
  const hijas = subtareasDePadre(parentId, tareas);
  const hechas = hijas.filter((s) => normEstado(s.estado) === 'HECHA').length;
  return { total: hijas.length, hechas };
}

function buildKanbanColumn(estado: string, tareas: Tarea[], tareasRaiz: Tarea[]): KanbanBlock[] {
  const estadoNorm = normEstado(estado);
  const blocks: KanbanBlock[] = [];

  const raizEnColumna = tareasRaiz
    .filter((t) => normEstado(t.estado) === estadoNorm)
    .sort((a, b) => a.orden - b.orden);

  const profesorEnColumna = new Set<number>();

  for (const t of raizEnColumna) {
    if (t.origen === 'PROFESOR') {
      profesorEnColumna.add(t.id);
      const hijasAqui = subtareasDePadre(t.id, tareas).filter(
        (s) => normEstado(s.estado) === estadoNorm,
      );
      blocks.push({ type: 'profesor-group', parent: t, subtareasEnColumna: hijasAqui });
    } else {
      blocks.push({ type: 'root', tarea: t });
    }
  }

  const sueltas = tareas
    .filter((t) => t.tareaPadreId && normEstado(t.estado) === estadoNorm)
    .filter((t) => !profesorEnColumna.has(t.tareaPadreId!));

  for (const sub of sueltas) {
    const parent = tareas.find((p) => p.id === sub.tareaPadreId);
    if (parent) blocks.push({ type: 'subtarea-suelta', tarea: sub, parent });
  }

  return blocks;
}

export default function ProyectoDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { refreshPerfil, user } = useAuth();
  const soloLectura = location.pathname.startsWith('/profesor/proyectos');
  const navProfesor = (location.state as ProfesorNavState | null) ?? {};
  const proyectoId = Number(id);
  const [tab, setTab] = useState<Tab>('tareas');
  const [proyecto, setProyecto] = useState<Proyecto | null>(null);
  const [tareas, setTareas] = useState<Tarea[]>([]);
  const [hitos, setHitos] = useState<Hito[]>([]);
  const [miembros, setMiembros] = useState<Miembro[]>([]);
  const [invitaciones, setInvitaciones] = useState<InvitacionProyecto[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTarea, setEditingTarea] = useState<Tarea | null>(null);
  const [savingTarea, setSavingTarea] = useState(false);
  const [draggingId, setDraggingId] = useState<number | null>(null);
  const [dropTarget, setDropTarget] = useState<string | null>(null);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteRol, setInviteRol] = useState<string>('DEVELOPER');
  const [inviting, setInviting] = useState(false);
  const [removingMiembroId, setRemovingMiembroId] = useState<number | null>(null);
  const [abandonando, setAbandonando] = useState(false);
  const [hitoModalOpen, setHitoModalOpen] = useState(false);
  const [editingHito, setEditingHito] = useState<Hito | null>(null);
  const [savingHito, setSavingHito] = useState(false);
  const [subtaskParent, setSubtaskParent] = useState<Tarea | null>(null);
  const [subtaskModalOpen, setSubtaskModalOpen] = useState(false);

  const asignables = useMemo(() => miembrosAsignables(miembros), [miembros]);
  const puedeModificar = useMemo(
    () => !soloLectura && esProductOwner(miembros, user?.uid, proyecto?.propietarioUid),
    [soloLectura, miembros, user?.uid, proyecto?.propietarioUid],
  );
  const miembroActual = useMemo(
    () => miembros.find((m) => m.usuarioUid === user?.uid),
    [miembros, user?.uid],
  );
  const puedeAbandonar = useMemo(
    () => !soloLectura
      && miembroActual != null
      && miembroActual.rol !== 'TUTOR'
      && proyecto?.propietarioUid !== user?.uid,
    [soloLectura, miembroActual, proyecto?.propietarioUid, user?.uid],
  );
  const tareasRaiz = useMemo(() => tareas.filter((t) => !t.tareaPadreId), [tareas]);
  const kanbanPorColumna = useMemo(() => {
    const map = new Map<string, KanbanBlock[]>();
    for (const estado of ESTADOS_TAREA) {
      map.set(estado, buildKanbanColumn(estado, tareas, tareasRaiz));
    }
    return map;
  }, [tareas, tareasRaiz]);

  const [highlightParentId, setHighlightParentId] = useState<number | null>(null);

  async function reloadHitos() {
    setHitos(await api.hitos.list(proyectoId));
  }

  function openCreateHitoModal() {
    setEditingHito(null);
    setHitoModalOpen(true);
  }

  function openEditHitoModal(hito: Hito) {
    setEditingHito(hito);
    setHitoModalOpen(true);
  }

  function closeHitoModal() {
    if (savingHito) return;
    setHitoModalOpen(false);
    setEditingHito(null);
  }

  async function saveHito(body: HitoInput) {
    setSavingHito(true);
    setError(null);
    try {
      if (editingHito) {
        await api.hitos.update(proyectoId, editingHito.id, body);
      } else {
        await api.hitos.create(proyectoId, body);
      }
      await reloadHitos();
      setHitoModalOpen(false);
      setEditingHito(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar el hito');
    } finally {
      setSavingHito(false);
    }
  }

  async function removeHito(hitoId: number) {
    if (!window.confirm('¿Eliminar este hito?')) return;
    setError(null);
    try {
      await api.hitos.delete(proyectoId, hitoId);
      await reloadHitos();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar el hito');
    }
  }

  async function toggleHitoCompletado(hito: Hito) {
    setError(null);
    try {
      await api.hitos.update(proyectoId, hito.id, {
        ...toHitoInput(hito),
        completado: !hito.completado,
      });
      await reloadHitos();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo actualizar el hito');
    }
  }

  async function reloadTareas() {
    setTareas(await api.tareas.list(proyectoId));
  }

  function openCreateModal() {
    setEditingTarea(null);
    setModalOpen(true);
  }

  function openEditModal(tarea: Tarea) {
    setEditingTarea(tarea);
    setModalOpen(true);
  }

  function closeModal() {
    if (savingTarea) return;
    setModalOpen(false);
    setEditingTarea(null);
  }

  function closeSubtaskModal() {
    if (savingTarea) return;
    setSubtaskModalOpen(false);
    setSubtaskParent(null);
  }

  function openSubtaskModal(parent: Tarea) {
    setSubtaskParent(parent);
    setSubtaskModalOpen(true);
  }

  async function saveTarea(body: TareaInput) {
    setSavingTarea(true);
    setError(null);
    try {
      if (editingTarea) {
        await api.tareas.update(proyectoId, editingTarea.id, body);
      } else if (subtaskParent) {
        await api.tareas.create(proyectoId, {
          ...body,
          tareaPadreId: subtaskParent.id,
          estado: 'PENDIENTE',
          prioridad: 'MEDIA',
          orden: subtaskParent.orden,
        });
      } else {
        await api.tareas.create(proyectoId, {
          ...body,
          orden: tareasRaiz.length + 1,
        });
      }
      await reloadTareas();
      setModalOpen(false);
      setEditingTarea(null);
      setSubtaskModalOpen(false);
      setSubtaskParent(null);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar la tarea');
    } finally {
      setSavingTarea(false);
    }
  }

  async function removeTarea(tareaId: number) {
    if (!window.confirm('¿Eliminar esta tarea?')) return;
    setError(null);
    try {
      await api.tareas.delete(proyectoId, tareaId);
      await reloadTareas();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar la tarea');
    }
  }

  async function moveTarea(tareaId: number, nuevoEstado: string) {
    const tarea = tareas.find((t) => t.id === tareaId);
    if (!tarea || normEstado(tarea.estado) === normEstado(nuevoEstado)) return;

    const hijas = !tarea.tareaPadreId ? subtareasDePadre(tarea.id, tareas) : [];
    const sinSubtareas = !tarea.tareaPadreId && hijas.length === 0;

    if (normEstado(nuevoEstado) !== 'PENDIENTE') {
      if (tarea.tareaPadreId) {
        if (!tarea.responsableUid) {
          setError('Asigna un responsable a la subtarea antes de moverla fuera de Pendiente');
          return;
        }
      } else if (sinSubtareas && !tarea.responsableUid) {
        setError('Asigna un responsable a la tarea antes de moverla fuera de Pendiente');
        return;
      }
    }

    setError(null);
    try {
      await api.tareas.update(proyectoId, tareaId, { ...toInput(tarea), estado: nuevoEstado });
      await reloadTareas();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo mover la tarea');
    }
  }

  async function updateResponsable(tarea: Tarea, responsableUid: string) {
    setError(null);
    try {
      await api.tareas.update(proyectoId, tarea.id, {
        ...toInput(tarea),
        responsableUid: responsableUid || undefined,
      });
      if (tarea.origen === 'PROFESOR' && !tarea.tareaPadreId) {
        const hijas = subtareasDePadre(tarea.id, tareas);
        if (hijas.length === 1) {
          await api.tareas.update(proyectoId, hijas[0].id, {
            ...toInput(hijas[0]),
            responsableUid: responsableUid || undefined,
          });
        }
      }
      await reloadTareas();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo asignar el responsable');
    }
  }

  async function reloadMiembros() {
    setMiembros(await api.miembros.list(proyectoId));
  }

  async function reloadInvitaciones() {
    if (soloLectura) {
      setInvitaciones([]);
      return;
    }
    setInvitaciones(await api.invitaciones.list(proyectoId));
  }

  async function invitarMiembro() {
    const email = inviteEmail.trim();
    if (!email) return;
    setInviting(true);
    setError(null);
    try {
      await api.miembros.invite(proyectoId, { email, rol: inviteRol });
      setInviteEmail('');
      setInviteRol('DEVELOPER');
      await reloadInvitaciones();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo invitar al miembro');
    } finally {
      setInviting(false);
    }
  }

  async function quitarMiembro(miembroId: number, nombre: string) {
    if (!window.confirm(`¿Quitar a ${nombre} del proyecto?`)) return;
    setRemovingMiembroId(miembroId);
    setError(null);
    try {
      await api.miembros.remove(proyectoId, miembroId);
      await reloadMiembros();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo quitar al miembro');
    } finally {
      setRemovingMiembroId(null);
    }
  }

  async function abandonarProyecto() {
    if (!proyecto) return;
    if (!window.confirm(`¿Abandonar el proyecto «${proyecto.titulo}»? Dejarás de tener acceso a él.`)) {
      return;
    }
    setAbandonando(true);
    setError(null);
    try {
      await api.miembros.abandonar(proyectoId);
      await refreshPerfil();
      navigate('/proyectos', { replace: true });
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo abandonar el proyecto');
      setAbandonando(false);
    }
  }

  const cargarDatos = useCallback(async () => {
    if (Number.isNaN(proyectoId)) {
      setError('Proyecto no válido');
      return;
    }
    setError(null);
    try {
      setProyecto(await api.proyectos.get(proyectoId));
      setTareas(await api.tareas.list(proyectoId));
      setHitos(await api.hitos.list(proyectoId));
      setMiembros(await api.miembros.list(proyectoId));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar datos del proyecto');
      return;
    }
    if (!soloLectura) {
      try {
        setInvitaciones(await api.invitaciones.list(proyectoId));
      } catch {
        setInvitaciones([]);
      }
    } else {
      setInvitaciones([]);
    }
  }, [proyectoId, soloLectura]);

  useEffect(() => {
    setProyecto(null);
    void cargarDatos();
  }, [cargarDatos]);

  useEffect(() => {
    if (!soloLectura) return;
    const interval = setInterval(() => void cargarDatos(), 45000);
    return () => clearInterval(interval);
  }, [soloLectura, cargarDatos]);

  function nombreMiembro(uid?: string) {
    if (!uid) return 'Sin asignar';
    return miembros.find((m) => m.usuarioUid === uid)?.nombre ?? uid;
  }

  function shouldCancelDrag(e: DragEvent<HTMLDivElement>) {
    return Boolean((e.target as HTMLElement).closest('button, select, input, textarea, a, label'));
  }

  function dragHandlers(tareaId: number, parentId?: number) {
    if (!puedeModificar) return {};
    return {
      draggable: true,
      onDragStart: (e: DragEvent<HTMLDivElement>) => {
        if (shouldCancelDrag(e)) {
          e.preventDefault();
          return;
        }
        e.dataTransfer.setData('tareaId', String(tareaId));
        setDraggingId(tareaId);
        if (parentId) setHighlightParentId(parentId);
      },
      onDragEnd: () => {
        setDraggingId(null);
        setDropTarget(null);
        setHighlightParentId(null);
      },
    };
  }

  function renderSubtareaCard(sub: Tarea, parent: Tarea, attached: boolean) {
    const destacada = highlightParentId === parent.id;
    return (
      <div
        key={sub.id}
        className={`kanban-card kanban-card--subtarea${attached ? ' kanban-card--subtarea-attached' : ' kanban-card--subtarea-detached'}${destacada ? ' kanban-card--linked-highlight' : ''}${puedeModificar && draggingId === sub.id ? ' kanban-card-dragging' : ''}`}
        {...dragHandlers(sub.id, parent.id)}
      >
        {!attached && (
          <span className="kanban-subtarea-parent-link" title={parent.titulo}>
            ↳ {parent.titulo}
          </span>
        )}
        <div className="kanban-card-title">
          <span className="kanban-subtarea-letter">{sub.letraSubtarea}</span>
          <span className="kanban-subtarea-sep">·</span>
          {sub.titulo}
        </div>
        {sub.descripcion && (
          <p className="kanban-card-desc">{sub.descripcion}</p>
        )}
        {!puedeModificar ? (
          <small className="muted" style={{ display: 'block', marginTop: '0.35rem' }}>
            {nombreMiembro(sub.responsableUid)}
          </small>
        ) : (
          <>
            <div className="form-group" style={{ marginTop: '0.5rem' }}>
              <label style={{ fontSize: '0.75rem' }}>Responsable</label>
              <select
                value={sub.responsableUid ?? ''}
                onChange={(e) => void updateResponsable(sub, e.target.value)}
              >
                <option value="">Sin asignar</option>
                {asignables.map((m) => (
                  <option key={m.usuarioUid} value={m.usuarioUid}>
                    {m.nombre}
                  </option>
                ))}
              </select>
            </div>
            <div className="kanban-card-actions">
              <button type="button" className="btn btn-secondary" onClick={() => openEditModal(sub)}>
                Editar
              </button>
              <button type="button" className="btn btn-danger" onClick={() => void removeTarea(sub.id)}>
                Eliminar
              </button>
            </div>
          </>
        )}
      </div>
    );
  }

  function renderProfesorHeader(parent: Tarea) {
    const { total, hechas } = progresoSubtareas(parent.id, tareas);
    const destacada = highlightParentId === parent.id;
    return (
      <div
        key={parent.id}
        className={`kanban-card kanban-card--profesor kanban-card--profesor-header${destacada ? ' kanban-card--linked-highlight' : ''}${puedeModificar && draggingId === parent.id ? ' kanban-card-dragging' : ''}`}
        {...dragHandlers(parent.id, parent.id)}
      >
        <div className="kanban-card-title">
          {parent.titulo}
          <span className="badge badge-profesor" style={{ marginLeft: '0.5rem' }}>
            Profesor
          </span>
        </div>
        {parent.descripcion && (
          <p className="kanban-card-desc">{parent.descripcion}</p>
        )}
        {parent.fechaLimite && (
          <small className="kanban-card-meta">Límite: {formatDate(parent.fechaLimite)}</small>
        )}
        {total > 0 ? (
          <p className="kanban-profesor-progress">
            {hechas}/{total} subtareas hechas
          </p>
        ) : !puedeModificar ? (
          <small className="muted" style={{ display: 'block', marginTop: '0.35rem' }}>
            Responsable: {nombreMiembro(parent.responsableUid)}
          </small>
        ) : (
          <div className="form-group" style={{ marginTop: '0.5rem' }}>
            <label style={{ fontSize: '0.75rem' }}>Responsable</label>
            <select
              value={parent.responsableUid ?? ''}
              onChange={(e) => void updateResponsable(parent, e.target.value)}
            >
              <option value="">Sin asignar</option>
              {asignables.map((m) => (
                <option key={m.usuarioUid} value={m.usuarioUid}>
                  {m.nombre}
                </option>
              ))}
            </select>
          </div>
        )}
        {puedeModificar && (
          <div className="kanban-card-actions">
            <button type="button" className="btn btn-secondary" onClick={() => openSubtaskModal(parent)}>
              + Subtarea
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => openEditModal(parent)}>
              Editar
            </button>
          </div>
        )}
      </div>
    );
  }

  function renderRootCard(t: Tarea) {
    return (
      <div
        key={t.id}
        className={`kanban-card${puedeModificar && draggingId === t.id ? ' kanban-card-dragging' : ''}`}
        {...dragHandlers(t.id)}
      >
        <div className="kanban-card-title">{t.titulo}</div>
        {t.descripcion && <p className="kanban-card-desc">{t.descripcion}</p>}
        <span className="badge">{etiquetaPrioridadTarea(t.prioridad)}</span>
        {t.fechaLimite && (
          <small className="kanban-card-meta">Límite: {formatDate(t.fechaLimite)}</small>
        )}
        {!puedeModificar ? (
          <small className="muted" style={{ display: 'block', marginTop: '0.35rem' }}>
            Responsable: {nombreMiembro(t.responsableUid)}
          </small>
        ) : (
          <>
            <div className="form-group" style={{ marginTop: '0.5rem' }}>
              <label style={{ fontSize: '0.75rem' }}>Responsable</label>
              <select
                value={t.responsableUid ?? ''}
                onChange={(e) => void updateResponsable(t, e.target.value)}
              >
                <option value="">Sin asignar</option>
                {asignables.map((m) => (
                  <option key={m.usuarioUid} value={m.usuarioUid}>
                    {m.nombre}
                  </option>
                ))}
              </select>
            </div>
            <div className="kanban-card-actions">
              <button type="button" className="btn btn-secondary" onClick={() => openEditModal(t)}>
                Editar
              </button>
              <button type="button" className="btn btn-danger" onClick={() => void removeTarea(t.id)}>
                Eliminar
              </button>
            </div>
          </>
        )}
      </div>
    );
  }

  function renderKanbanBlock(block: KanbanBlock) {
    if (block.type === 'profesor-group') {
      return (
        <div key={`group-${block.parent.id}`} className="kanban-group">
          {renderProfesorHeader(block.parent)}
          {block.subtareasEnColumna.length > 0 && (
            <div className="kanban-group-children">
              {block.subtareasEnColumna.map((sub) => renderSubtareaCard(sub, block.parent, true))}
            </div>
          )}
        </div>
      );
    }
    if (block.type === 'subtarea-suelta') {
      return renderSubtareaCard(block.tarea, block.parent, false);
    }
    return renderRootCard(block.tarea);
  }

  async function eliminarProyecto() {
    if (!proyecto) return;
    if (
      !window.confirm(
        `¿Eliminar el proyecto «${proyecto.titulo}»? Se borrarán también tareas e hitos asociados.`,
      )
    ) {
      return;
    }
    setDeleting(true);
    setError(null);
    try {
      await api.proyectos.delete(proyectoId);
      await refreshPerfil();
      navigate('/proyectos', { replace: true });
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar el proyecto');
      setDeleting(false);
    }
  }

  if (error && !proyecto) return <div className="alert alert-warn">{error}</div>;
  if (!proyecto) return <p>Cargando proyecto…</p>;

  const progresoCronograma = progresoHitos(hitos);
  const hitosFueraDeRango = hitos.filter((h) => hitoFueraDeRango(h.fecha, proyecto));

  const breadcrumbItems = soloLectura
    ? [
        { label: 'Portal', to: '/profesor' },
        ...(navProfesor.asignaturaNombre && navProfesor.asignaturaId
          ? [{ label: navProfesor.asignaturaNombre, to: `/profesor/asignaturas/${navProfesor.asignaturaId}` }]
          : []),
        ...(navProfesor.plantillaTitulo && navProfesor.plantillaId && navProfesor.asignaturaId
          ? [{
              label: navProfesor.plantillaTitulo,
              to: `/profesor/asignaturas/${navProfesor.asignaturaId}/plantillas/${navProfesor.plantillaId}`,
            }]
          : []),
        { label: proyecto.titulo },
      ]
    : [
        { label: 'Inicio', to: '/' },
        { label: 'Proyectos', to: '/proyectos' },
        { label: proyecto.titulo },
      ];

  return (
    <>
      <Breadcrumbs items={breadcrumbItems} />

      {soloLectura && (
        <div className="alert alert-ok profesor-supervision-banner">
          Vista de supervisión (solo lectura). Los cambios de los alumnos se actualizan automáticamente.
        </div>
      )}

      {!soloLectura && !puedeModificar && miembros.length > 0 && (
        <div className="alert alert-ok">
          Vista de solo lectura. Solo el Product Owner puede modificar el proyecto.
        </div>
      )}

      <div className="page-header page-header-row">
        <div>
          <h2>{proyecto.titulo}</h2>
          <p>{proyecto.descripcion?.trim() ? proyecto.descripcion : 'Sin descripción'}</p>
        </div>
        {puedeModificar && (
        <div className="page-header-actions">
          <Link to={`/proyectos/${proyectoId}/editar`} className="btn btn-secondary">
            Editar
          </Link>
          <button
            type="button"
            className="btn btn-danger"
            disabled={deleting}
            onClick={eliminarProyecto}
          >
            {deleting ? 'Eliminando…' : 'Eliminar'}
          </button>
        </div>
        )}
        {puedeAbandonar && (
          <div className="page-header-actions">
            <button
              type="button"
              className="btn btn-secondary"
              disabled={abandonando}
              onClick={() => void abandonarProyecto()}
            >
              {abandonando ? 'Abandonando…' : 'Abandonar proyecto'}
            </button>
          </div>
        )}
      </div>

      <div className="card project-summary">
        <span className={`badge badge-estado badge-${claseBadgeEstadoProyecto(proyecto.estado)}`}>
          {etiquetaEstadoProyecto(proyecto.estado)}
        </span>
        <dl className="project-meta project-meta-inline">
          {proyecto.asignaturaNombre && (
            <div>
              <dt>Asignatura</dt>
              <dd>{proyecto.asignaturaNombre}</dd>
            </div>
          )}
          <div>
            <dt>Inicio</dt>
            <dd>{formatDate(proyecto.fechaInicio)}</dd>
          </div>
          <div>
            <dt>Fin</dt>
            <dd>{formatDate(proyecto.fechaFin)}</dd>
          </div>
          <div>
            <dt>Actualizado</dt>
            <dd>{formatDate(proyecto.actualizadoEn?.slice(0, 10))}</dd>
          </div>
          <div>
            <dt>Tareas</dt>
            <dd>{tareasRaiz.length}</dd>
          </div>
        </dl>
      </div>

      {error && <div className="alert alert-warn">{error}</div>}

      <div className="tabs">
        <button type="button" className={tab === 'tareas' ? 'active' : ''} onClick={() => setTab('tareas')}>
          Tareas
        </button>
        <button type="button" className={tab === 'cronograma' ? 'active' : ''} onClick={() => setTab('cronograma')}>
          Cronograma
        </button>
        <button type="button" className={tab === 'equipo' ? 'active' : ''} onClick={() => setTab('equipo')}>
          Equipo
        </button>
      </div>

      {tab === 'tareas' && (
        <>
          <div className="card page-header-row">
            <div>
              <h3 style={{ margin: 0 }}>Tablero Kanban</h3>
              <p className="muted" style={{ margin: '0.25rem 0 0', fontSize: '0.9rem' }}>
                {soloLectura
                  ? 'Estado actual del tablero del grupo (solo lectura).'
                  : puedeModificar
                    ? 'Arrastra las tareas entre columnas. Al mover la tarea del profesor, solo las subtareas de su columna anterior la acompañan; las que ya están más adelante no se mueven.'
                    : 'Estado actual del tablero (solo lectura). Solo el Product Owner puede modificar tareas.'}
              </p>
            </div>
            {puedeModificar && (
            <button type="button" className="btn" onClick={openCreateModal}>
              Nueva tarea
            </button>
            )}
          </div>
          <div className="kanban">
            {ESTADOS_TAREA.map((estado) => (
              <div
                key={estado}
                className={`kanban-col${puedeModificar && dropTarget === estado ? ' kanban-col-drop' : ''}`}
                onDragOver={!puedeModificar ? undefined : (e) => {
                  e.preventDefault();
                  setDropTarget(estado);
                }}
                onDragLeave={!puedeModificar ? undefined : () => setDropTarget(null)}
                onDrop={!puedeModificar ? undefined : (e) => {
                  e.preventDefault();
                  setDropTarget(null);
                  const tareaId = Number(e.dataTransfer.getData('tareaId'));
                  if (!Number.isNaN(tareaId)) void moveTarea(tareaId, estado);
                }}
              >
                <h4>{etiquetaEstadoTarea(estado)}</h4>
                {(kanbanPorColumna.get(estado) ?? []).map(renderKanbanBlock)}
              </div>
            ))}
          </div>
        </>
      )}

      {puedeModificar && (
      <TareaFormModal
        open={modalOpen}
        title={editingTarea ? 'Editar tarea' : 'Nueva tarea'}
        initial={editingTarea ?? undefined}
        miembros={miembros}
        tituloBloqueado={editingTarea?.origen === 'PROFESOR' && !editingTarea.tareaPadreId}
        saving={savingTarea}
        onClose={closeModal}
        onSubmit={saveTarea}
      />
      )}
      {puedeModificar && (
      <TareaFormModal
        open={subtaskModalOpen}
        title={subtaskParent ? `Nueva subtarea — ${subtaskParent.titulo}` : 'Nueva subtarea'}
        miembros={miembros}
        modo="subtarea"
        saving={savingTarea}
        onClose={closeSubtaskModal}
        onSubmit={saveTarea}
      />
      )}

      {tab === 'cronograma' && (
        <>
          <div className="card page-header-row">
            <div>
              <h3 style={{ margin: 0 }}>Cronograma</h3>
              <p className="muted" style={{ margin: '0.25rem 0 0', fontSize: '0.9rem' }}>
                {soloLectura
                  ? 'Hitos y diagrama de Gantt del grupo.'
                  : 'Gestiona los hitos del proyecto y visualízalos en el diagrama de Gantt.'}
              </p>
            </div>
            {puedeModificar && (
            <button type="button" className="btn" onClick={openCreateHitoModal}>
              Nuevo hito
            </button>
            )}
          </div>

          {hitos.length > 0 && (
            <div className="card cronograma-progreso">
              <div className="cronograma-progreso-header">
                <strong>
                  {progresoCronograma.completados} de {progresoCronograma.total} hitos completados
                </strong>
                <span className="muted">{progresoCronograma.porcentaje}%</span>
              </div>
              <div className="progress-bar" role="progressbar" aria-valuenow={progresoCronograma.porcentaje} aria-valuemin={0} aria-valuemax={100}>
                <div
                  className="progress-bar-fill"
                  style={{ width: `${progresoCronograma.porcentaje}%` }}
                />
              </div>
            </div>
          )}

          {hitosFueraDeRango.length > 0 && (
            <div className="alert alert-warn">
              {hitosFueraDeRango.length === 1
                ? '1 hito tiene una fecha fuera del periodo del proyecto'
                : `${hitosFueraDeRango.length} hitos tienen fechas fuera del periodo del proyecto`}
              {' '}
              ({formatDate(proyecto.fechaInicio)} – {formatDate(proyecto.fechaFin)}).
            </div>
          )}

          <div className="card">
            <div className="timeline" style={{ marginBottom: '1rem' }}>
              {hitos.length === 0 && (
                <p className="muted">Aún no hay hitos. Crea uno con «Nuevo hito».</p>
              )}
              {hitos.map((h) => {
                const fueraDeRango = hitoFueraDeRango(h.fecha, proyecto);
                return (
                <div
                  key={h.id}
                  className={`timeline-item${fueraDeRango ? ' timeline-item-warn' : ''}`}
                  style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem' }}
                >
                  <div>
                    <strong>{h.titulo}</strong>
                    <div style={{ color: 'var(--muted)', fontSize: '0.85rem' }}>
                      {formatDate(h.fecha)}{' '}
                      <span className={`badge${h.completado ? ' badge-ok' : ''}`}>
                        {h.completado ? 'Completado' : 'Pendiente'}
                      </span>
                      {fueraDeRango && (
                        <span className="badge badge-warn-outline" style={{ marginLeft: '0.35rem' }}>
                          Fuera de rango
                        </span>
                      )}
                    </div>
                  </div>
                  {puedeModificar && (
                  <div style={{ display: 'flex', gap: '0.35rem', flexShrink: 0 }}>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      style={{ fontSize: '0.8rem', padding: '0.35rem 0.6rem' }}
                      onClick={() => void toggleHitoCompletado(h)}
                    >
                      {h.completado ? 'Marcar pendiente' : 'Marcar completado'}
                    </button>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      style={{ fontSize: '0.8rem', padding: '0.35rem 0.6rem' }}
                      onClick={() => openEditHitoModal(h)}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger"
                      style={{ fontSize: '0.8rem', padding: '0.35rem 0.6rem' }}
                      onClick={() => void removeHito(h.id)}
                    >
                      Eliminar
                    </button>
                  </div>
                  )}
                </div>
              );
              })}
            </div>
            <GanttChart proyecto={proyecto} hitos={hitos} tareas={tareas} />
          </div>
        </>
      )}

      {puedeModificar && (
      <HitoFormModal
        open={hitoModalOpen}
        title={editingHito ? 'Editar hito' : 'Nuevo hito'}
        initial={editingHito ?? undefined}
        proyecto={proyecto}
        saving={savingHito}
        onClose={closeHitoModal}
        onSubmit={saveHito}
      />
      )}

      {tab === 'equipo' && (
        <>
          <table className="table card">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Email</th>
                <th>Rol</th>
                {puedeModificar && <th />}
              </tr>
            </thead>
            <tbody>
              {miembros.length === 0 && invitaciones.length === 0 && (
                <tr>
                  <td colSpan={puedeModificar ? 4 : 3} className="muted">
                    No hay miembros en este proyecto.
                  </td>
                </tr>
              )}
              {invitaciones.map((inv) => (
                <tr key={`inv-${inv.id}`} className="muted">
                  <td>{inv.nombre}</td>
                  <td>{inv.email}</td>
                  <td>
                    {etiquetaRol(inv.rol, inv.rolEtiqueta)}
                    <span style={{ marginLeft: '0.5rem', fontSize: '0.8rem' }}>(invitado, pendiente)</span>
                  </td>
                  {puedeModificar && <td />}
                </tr>
              ))}
              {miembros.map((m) => (
                <tr key={m.id}>
                  <td>
                    {!puedeModificar ? (
                      <Link
                        to={`/profesor/alumnos/${encodeURIComponent(m.usuarioUid)}`}
                        state={navProfesor}
                      >
                        {m.nombre}
                      </Link>
                    ) : (
                      m.nombre
                    )}
                  </td>
                  <td>{m.email}</td>
                  <td>{etiquetaRol(m.rol, m.rolEtiqueta)}</td>
                  {puedeModificar && (
                  <td style={{ textAlign: 'right' }}>
                    {m.rol !== 'TUTOR' ? (
                    <button
                      type="button"
                      className="btn btn-danger"
                      style={{ fontSize: '0.8rem', padding: '0.35rem 0.6rem' }}
                      disabled={removingMiembroId === m.id}
                      onClick={() => void quitarMiembro(m.id, m.nombre)}
                    >
                      {removingMiembroId === m.id ? 'Quitando…' : 'Quitar'}
                    </button>
                    ) : (
                      <span className="muted" style={{ fontSize: '0.8rem' }}>Tutor</span>
                    )}
                  </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
          {puedeModificar && (
          <div className="card">
            <h3 style={{ marginTop: 0 }}>Invitar miembro</h3>
            <p className="muted" style={{ fontSize: '0.9rem' }}>
              El usuario debe estar registrado y matriculado en la asignatura del proyecto (si aplica).
            </p>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="invite-email">Email</label>
                <input
                  id="invite-email"
                  type="email"
                  placeholder="compañero@upsa.es"
                  value={inviteEmail}
                  onChange={(e) => setInviteEmail(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label htmlFor="invite-rol">Rol</label>
                <select
                  id="invite-rol"
                  value={inviteRol}
                  onChange={(e) => setInviteRol(e.target.value)}
                >
                  {ROLES_INVITACION.map((rol) => (
                    <option key={rol} value={rol}>
                      {etiquetaRol(rol)}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <button
              type="button"
              className="btn"
              disabled={inviting || !inviteEmail.trim()}
              onClick={() => void invitarMiembro()}
            >
              {inviting ? 'Enviando invitación…' : 'Enviar invitación'}
            </button>
          </div>
          )}
        </>
      )}
    </>
  );
}
