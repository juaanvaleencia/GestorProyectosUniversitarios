# Casos de uso

Actores principales: **Estudiante** (usuario autenticado con Firebase). Los tutores de asignatura aparecen como miembros demo del proyecto (rol `TUTOR`), sin flujo de login propio.

---

## CU-01 Registro, inicio de sesión y perfil

**Actor:** Estudiante  
**Precondición:** Firebase configurado en frontend y backend.

**Flujo principal:**
1. El estudiante se registra o inicia sesión en la pantalla de login (Firebase Auth).
2. En el registro elige su **universidad** (UPSA o USAL).
3. Tras el login, el frontend llama a `POST /api/usuarios/sync` con el JWT.
4. Si el usuario no tiene universidad asignada, se redirige a **Completar perfil**.

**Resultado:** Sesión activa, usuario persistido en PostgreSQL y API autorizada con `Authorization: Bearer <token>`.

**Reglas:**
- La universidad solo se asigna en el registro o en completar perfil; no puede cambiarse después.
- En el primer sync se genera una notificación de bienvenida.

---

## CU-02 Gestionar proyectos

**Actor:** Estudiante autenticado con universidad asignada

**Flujos:**

### Proyecto libre
1. Proyectos → Nuevo proyecto → **Proyecto libre**.
2. Completa título, descripción, fechas y estado.
3. El sistema crea el proyecto; el usuario queda como **Product Owner**.

### Proyecto desde plantilla universitaria
1. Proyectos → Nuevo proyecto → **Desde mi universidad**.
2. Elige asignatura → plantilla del curso → revisa detalle (tareas e hitos previstos).
3. Pulsa **Crear proyecto con esta plantilla**.
4. El sistema crea el proyecto con datos de la plantilla, instancia **tareas** (estado `PENDIENTE`) e **hitos**, añade al tutor de la asignatura (rol `TUTOR`) y redirige al detalle.

**Otros:** listar proyectos propios o en los que es miembro, editar metadatos, eliminar proyecto.

**Reglas:**
- Solo se ven proyectos en los que el usuario es propietario o miembro.
- Un mismo usuario **no puede crear dos proyectos** con la misma plantilla (validación en backend).
- Varios grupos distintos pueden usar la misma plantilla.

**Resultado:** Proyectos persistidos en PostgreSQL con trazabilidad `plantilla_id` / `asignatura_id` cuando procede.

---

## CU-03 Gestionar tareas (tablero Kanban)

**Actor:** Estudiante miembro del proyecto

**Descripción:** En el detalle del proyecto, pestaña de tareas: columnas por estado (`PENDIENTE`, `EN_PROGRESO`, `REVISION`, `HECHA`).

**Operaciones:**
- Crear tarea (título, descripción, prioridad, responsable, fecha límite).
- Editar tarea.
- **Mover** tarea entre columnas (cambio de estado).
- Eliminar tarea.

**Resultado:** Tablero operativo con datos reales del microservicio `tareas-quarkus`.

---

## CU-04 Gestionar equipo del proyecto

**Actor:** Estudiante miembro del proyecto

**Descripción:** Pestaña **Equipo**: listado de miembros con rol y formulario de invitación.

**Flujo de invitación:**
1. Introduce el email de un compañero **ya registrado** en la plataforma.
2. Elige rol: Equipo de Desarrollo (`DEVELOPER`) o Scrum Master (`SCRUM_MASTER`).
3. El sistema añade al miembro y envía una **notificación** al invitado.

**Otras operaciones:** quitar miembro (no se puede eliminar al propietario).

**Resultado:** Equipo persistido; roles `PRODUCT_OWNER`, `SCRUM_MASTER`, `DEVELOPER` y `TUTOR` (este último al instanciar desde plantilla).

---

## CU-05 Gestionar cronograma e hitos

**Actor:** Estudiante miembro del proyecto

**Descripción:** Pestaña **Cronograma**: línea temporal de hitos y vista **Gantt** (proyecto, hitos y tareas).

**Operaciones:**
- Crear hito (título, fecha, completado).
- Editar hito.
- Marcar hito como completado o pendiente.
- Eliminar hito.

**Resultado:** Hitos persistidos en `proyectos-quarkus`; visualización integrada con fechas del proyecto y tareas.

---

## CU-06 Consultar informes

**Actor:** Estudiante autenticado

**Descripción:** Pantalla **Informes** con estadísticas calculadas en el microservicio `informes-quarkus` a partir de la BD del usuario: proyectos, tareas (completadas/pendientes) e hitos.

**Resultado:** Gráficos y resúmenes con datos reales filtrados por el usuario autenticado.

---

## CU-07 Consultar notificaciones

**Actor:** Estudiante autenticado

**Descripción:** Pantalla **Notificaciones** con avisos del usuario (bienvenida, invitación a proyecto, etc.).

**Resultado:** Listado de notificaciones con indicador visual de leída/no leída. *(Marcar como leída desde la UI: pendiente de implementar.)*

---

## Catálogo académico (soporte de CU-02)

**Actor:** Estudiante autenticado

**Descripción:** Catálogo de solo lectura por universidad: asignaturas, plantillas de proyecto, tareas e hitos sugeridos. No tiene menú propio; se accede desde **Nuevo proyecto → Desde mi universidad**.

**API:** `GET /api/universidades/{id}/asignaturas`, `GET /api/asignaturas/{id}/plantillas`, `GET /api/plantillas/{id}`.

**Resultado:** El alumno solo accede al catálogo de **su** universidad.
