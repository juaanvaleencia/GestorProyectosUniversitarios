# Memoria técnica (resumen)

**Título:** Sistema web para la planificación y control de proyectos universitarios  
**Autor:** Juan Valencia — UPSA  
**Director:** Roberto Berjón Gallinas

## Qué es este entregable

Prototipo del TFG según el anteproyecto: pantallas navegables, login Firebase (JWT) y datos en PostgreSQL. La interfaz React consume solo el **aggregator** (puerto 8080). Kanban, invitaciones y edición de hitos son maquetas sin persistencia.

## Arquitectura (como `imdbFinal`)

- **Frontend:** React + Vite (el curso usa JSP; misma idea de cliente web).
- **Backend:** Maven multi-módulo, Java 21, microservicios Quarkus + `aggregator-quarkus`.
- **BD:** PostgreSQL en Docker (`database.sql`).

## Módulos y puertos

| Módulo | Puerto | Rol |
|--------|--------|-----|
| aggregator | 8080 | API única del frontend, CORS, reenvío JWT |
| health | 8081 | Health check |
| proyectos | 8082 | Proyectos, hitos, miembros |
| tareas | 8083 | Tareas por proyecto |
| usuarios | 8084 | Sync usuario, perfil, notificaciones |
| informes | 8085 | Resumen estadístico (consulta BD) |

## Equivalencia con `imdbFinal`

| imdbFinal | tfg-proyectos |
|-----------|---------------|
| `common` | `backend/common` |
| `personas-quarkus`, `peliculas-jee`, … | `proyectos-quarkus`, `tareas-quarkus`, … |
| `aggregator` + `Repository` / `RepositoryImpl` | Igual |
| `Dao` + `*Row` + `DaoMappers` + `RepositoryImpl` en `dao/` | Igual en microservicios con BD |
| `PersonasResource` → casos de uso | `ProyectosResource` → casos de uso |
| `ResponseMappers` + commands en REST | `ResponseMappers` → `AddProyectoCommand` / `ReplaceProyectoCommand` |
| `UsecaseMapper` (command → domain) | `UsecaseMapper` en proyectos |
| `ImdbExceptionMapper`, `ConstraintViolationExceptionMapper` | `TfgExceptionMapper`, `ConstraintViolationExceptionMapper` |
| `imdbRequests.http`, `aggregatorRequests.http` (raíz) | `tfgRequests.http`, `aggregatorRequests.http` |
| `personas.sh`, `peliculas.sh` (por módulo) | `proyectos.sh`, `usuarios.sh`, … (por módulo) |
| Frontend JSP | React (elección del TFG) |

## Capas por microservicio (patrón del profesor)

```
adapters/rest/          → *Resource, dtos, mappers/ResponseMappers, providers/
application/usecases/   → *Usecase + impl/
domain/repository/      → Repository (interfaz)
domain/model/
infrastructure/persistence/dao/
  Dao, impl/DaoImpl, dtos/*Row, mappers/DaoMappers, RepositoryImpl
```

El **aggregator** no tiene casos de uso ni DAO: solo `Repository` + `RepositoryImpl` + REST Clients (`infrastructure/rest/proyectos/`, etc.).

## Funcionalidad actual

| Área | Estado |
|------|--------|
| Login + JWT + sync usuario | Sí |
| Listar / crear / editar proyectos (por usuario) | Sí |
| Tareas, hitos, miembros | Lectura |
| Informes | Datos reales del usuario en BD |
| Kanban / invitar miembro | Maqueta (UI deshabilitada) |

## Arranque

1. `docker compose up -d` (raíz del proyecto)  
2. Primera vez: `cd backend && ./mvnw install -DskipTests -f ..`  
3. Todos los servicios: `cd backend && ./start-services.sh`  
4. Un solo módulo (como el profesor): `cd backend/proyectos-quarkus && ./proyectos.sh`  
5. Detener todo: `cd backend && ./stop-services.sh`  
6. Frontend: `cd frontend-react && npm run dev`

Pruebas HTTP: `tfgRequests.http` (microservicios) y `aggregatorRequests.http` (aggregator).

## Diferencias justificadas respecto a imdb

- Nombres de excepciones `Tfg*` en lugar de `Imdb*` (dominio del TFG).
- Módulo **health** y rutas bajo `/api/...` (API del anteproyecto).
- **Firebase OIDC** en lugar de otro IdP; el aggregator reenvía el token (`ForwardAuthorizationFactory`).
- Sin módulo JEE/WAR: todo Quarkus + React.

Detalle de arranque en el [README](../README.md).
