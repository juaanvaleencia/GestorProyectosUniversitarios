# API REST

El **frontend** solo consume el **aggregator** en `http://localhost:8080`.

Arquitectura hexagonal por microservicio (`domain` → `application` → `adapters` → `infrastructure`), alineada con el proyecto de referencia `imdbFinal`: cada Resource del aggregator delega en `Repository` → Rest Client → microservicio.

## Autenticación

| Ámbito | Rutas | Cabecera |
|--------|-------|----------|
| Públicas | `GET /api/health`, `GET /api/universidades` | No requerida |
| Resto de `/api/*` | Todas las demás | `Authorization: Bearer <JWT Firebase>` |

Tras login/registro, el frontend llama a `POST /api/usuarios/sync` para persistir el usuario en PostgreSQL.

CORS habilitado para `http://localhost:5173`.

## Puertos (desarrollo local)

| Módulo | Puerto | Responsabilidad |
|--------|--------|-----------------|
| `aggregator-quarkus` | **8080** | Puerta de entrada, CORS, reenvío JWT |
| `health-quarkus` | 8081 | Health check |
| `proyectos-quarkus` | 8082 | Proyectos, hitos, miembros, catálogo |
| `tareas-quarkus` | 8083 | Tareas por proyecto |
| `usuarios-quarkus` | 8084 | Usuarios, perfil, notificaciones |
| `informes-quarkus` | 8085 | Resumen estadístico |

Base de datos: PostgreSQL en `localhost:5433`, BD `tfg_proyectos`.

## Rutas del aggregator (`:8080`)

### Sistema y usuario

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/health` | Estado del servicio (público) |
| GET | `/api/dashboard` | Resumen compuesto del usuario |
| POST | `/api/usuarios/sync` | Sincronizar usuario tras login Firebase |
| GET | `/api/usuarios/perfil` | Perfil, participaciones y datos de universidad |
| GET | `/api/notificaciones` | Notificaciones del usuario |

### Universidades y catálogo académico

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/universidades` | Listado de universidades (público) |
| GET | `/api/universidades/{id}/asignaturas` | Asignaturas de la universidad (solo la del usuario) |
| GET | `/api/asignaturas/{id}/plantillas` | Plantillas de proyecto de una asignatura |
| GET | `/api/plantillas/{id}` | Detalle de plantilla (tareas e hitos sugeridos) |

### Proyectos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/proyectos` | Proyectos del usuario (propietario o miembro) |
| POST | `/api/proyectos` | Crear proyecto libre |
| POST | `/api/proyectos/desde-plantilla/{plantillaId}` | Crear proyecto desde plantilla (+ tareas, hitos, tutor) |
| GET | `/api/proyectos/{id}` | Detalle del proyecto |
| PUT | `/api/proyectos/{id}` | Actualizar proyecto |
| DELETE | `/api/proyectos/{id}` | Eliminar proyecto |

### Tareas (microservicio `tareas-quarkus`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/proyectos/{id}/tareas` | Listar tareas |
| POST | `/api/proyectos/{id}/tareas` | Crear tarea |
| GET | `/api/proyectos/{id}/tareas/{tareaId}` | Detalle de tarea |
| PUT | `/api/proyectos/{id}/tareas/{tareaId}` | Actualizar tarea (incluye cambio de estado) |
| DELETE | `/api/proyectos/{id}/tareas/{tareaId}` | Eliminar tarea |

### Hitos (microservicio `proyectos-quarkus`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/proyectos/{id}/hitos` | Listar hitos |
| POST | `/api/proyectos/{id}/hitos` | Crear hito |
| PUT | `/api/proyectos/{id}/hitos/{hitoId}` | Actualizar hito |
| DELETE | `/api/proyectos/{id}/hitos/{hitoId}` | Eliminar hito |

### Miembros (microservicio `proyectos-quarkus`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/proyectos/{id}/miembros` | Listar miembros |
| POST | `/api/proyectos/{id}/miembros` | Invitar miembro por email |
| DELETE | `/api/proyectos/{id}/miembros/{miembroId}` | Quitar miembro |

### Informes

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/informes/resumen` | Estadísticas de proyectos, tareas e hitos del usuario |

## Códigos de error habituales

| HTTP | Significado típico |
|------|-------------------|
| 401 | Token ausente o inválido |
| 403 | Sin permiso (p. ej. catálogo de otra universidad) |
| 404 | Recurso no encontrado |
| 400 | Validación (p. ej. email no registrado, plantilla duplicada) |

## Pruebas HTTP

- `aggregatorRequests.http` — pruebas contra el aggregator (recomendado para el frontend).
- `tfgRequests.http` — pruebas directas a cada microservicio.

## Arranque del backend

```bash
cd backend
./mvnw install -DskipTests          # tras cambios en common
./mvnw package -DskipTests          # si faltan JARs
./start-services.sh                 # modo JAR por defecto
./stop-services.sh
```

Modo desarrollo con recarga: `TFG_START_MODE=dev ./start-services.sh`.

Un solo módulo (estilo profesor): `cd backend/proyectos-quarkus && ./proyectos.sh`.
