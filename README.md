# TFG — Planificación de proyectos universitarios (UPSA)

Prototipo del Trabajo Fin de Grado: login Firebase (JWT), pantallas de proyectos, tareas, informes y perfil. Arquitectura alineada con el proyecto de clase `imdbFinal` (microservicios Quarkus + aggregator + capas domain/application/adapters).

## Estructura del proyecto

```
tfg-proyectos/
├── backend/
│   ├── common/
│   ├── health-quarkus/         # health.sh
│   ├── proyectos-quarkus/      # proyectos.sh
│   ├── tareas-quarkus/         # tareas.sh
│   ├── usuarios-quarkus/       # usuarios.sh
│   ├── informes-quarkus/       # informes.sh
│   ├── aggregator-quarkus/     # aggregator.sh
│   ├── start-services.sh       # Arranca todo en segundo plano (desarrollo)
│   └── stop-services.sh
├── frontend-react/
├── database.sql
├── docker-compose.yml
├── tfgRequests.http            # Pruebas a microservicios (como imdbRequests.http)
├── aggregatorRequests.http     # Pruebas al aggregator
└── docs/
```

## Requisitos

- JDK **21**
- Docker (PostgreSQL)
- Node.js + npm (frontend)

## 1. Base de datos

```bash
docker compose up -d
```

PostgreSQL en **localhost:5433** (usuario `tfg`, BD `tfg_proyectos`).

## 2. Backend

Compilar (primera vez):

```bash
cd backend && ./mvnw install -DskipTests -f ..
```

**Arrancar todos los servicios** (desarrollo, logs en `backend/logs/`):

```bash
cd backend
chmod +x mvnw start-services.sh stop-services.sh */*.sh
./start-services.sh
./stop-services.sh   # detener
```

**Un solo microservicio** (mismo estilo que el profesor: un `.sh` por módulo):

```bash
cd backend/proyectos-quarkus && ./proyectos.sh
# Al final, en otra terminal: aggregator-quarkus/aggregator.sh
```

Orden recomendado si arrancas a mano: health → proyectos → tareas → usuarios → informes → **aggregator** (8080).

**IntelliJ:** `quarkus:dev` en cada módulo.

## 3. Frontend

```bash
cd frontend-react
cp .env.example .env
npm install
npm run dev
```

- App: http://localhost:5173  
- API: http://localhost:8080 (aggregator)

## Seguridad

Rutas `/api/*` (salvo health) requieren `Authorization: Bearer <token Firebase>`. Tras el login, el frontend llama a `POST /api/usuarios/sync`.

Recrear la BD:

```bash
docker compose down -v && docker compose up -d
```

## Documentación

- [Firebase](docs/FIREBASE.md)
- [API y puertos](docs/API.md)
- [Casos de uso](docs/CASOS_DE_USO.md)
- [Manual de usuario](docs/MANUAL_USUARIO.md)
- [Memoria técnica](docs/MEMORIA.md)

Director: Roberto Berjón Gallinas — UPSA
