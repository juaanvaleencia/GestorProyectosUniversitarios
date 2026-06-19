# API REST

El **frontend** solo llama al **aggregator** (`http://localhost:8080`).

Las rutas de negocio requieren `Authorization: Bearer <JWT Firebase>`, salvo health.

## Puertos

| Módulo | Puerto | Responsabilidad |
|--------|--------|-----------------|
| `aggregator-quarkus` | **8080** | Puerta de entrada, CORS, reenvío JWT |
| `health-quarkus` | 8081 | Health check |
| `proyectos-quarkus` | 8082 | Proyectos, hitos, miembros |
| `tareas-quarkus` | 8083 | Tareas por proyecto |
| `usuarios-quarkus` | 8084 | Sync usuario; notificaciones |
| `informes-quarkus` | 8085 | Resumen estadístico (BD) |

## Rutas (vía aggregator :8080)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/health` | Health |
| GET | `/api/dashboard` | Resumen compuesto |
| GET | `/api/proyectos` | Proyectos del usuario autenticado |
| POST | `/api/proyectos` | Crear proyecto |
| PUT | `/api/proyectos/{id}` | Editar proyecto |
| GET | `/api/proyectos/{id}` | Detalle |
| GET | `/api/proyectos/{id}/hitos` | Hitos |
| GET | `/api/proyectos/{id}/miembros` | Miembros |
| GET | `/api/proyectos/{id}/tareas` | Tareas |
| GET | `/api/notificaciones` | Notificaciones |
| GET | `/api/informes/resumen` | Estadísticas desde BD |
| POST | `/api/usuarios/sync` | Sincronizar usuario tras login |

**Maqueta (sin persistencia):** mover tareas Kanban, invitar miembros, editar hitos desde la UI.

## Pruebas HTTP

- `tfgRequests.http` — microservicios directos (como `imdbRequests.http`)
- `aggregatorRequests.http` — aggregator

## Arranque

```bash
cd backend
./start-services.sh
./stop-services.sh
```

Un módulo: `cd backend/proyectos-quarkus && ./proyectos.sh` (mismo patrón que el profesor).
