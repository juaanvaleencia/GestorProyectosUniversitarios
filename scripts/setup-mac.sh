#!/usr/bin/env bash
# Preparación inicial en macOS (ejecutar una vez tras clonar/copiar el proyecto)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "==> Permisos Maven Wrapper"
chmod +x backend/mvnw
chmod +x backend/start-services.sh backend/stop-services.sh 2>/dev/null || true

echo "==> Frontend: .env"
if [[ ! -f frontend-react/.env ]]; then
  cp frontend-react/.env.example frontend-react/.env
  echo "    Creado frontend-react/.env desde .env.example"
else
  echo "    frontend-react/.env ya existe"
fi

echo "==> Frontend: dependencias npm"
(
  cd frontend-react
  rm -rf node_modules
  npm install
)

echo "==> Backend: instalar dependencias Maven"
chmod +x backend/mvnw
if command -v mvn >/dev/null 2>&1; then
  mvn -q install -DskipTests -U
else
  (cd backend && ./mvnw -q install -DskipTests -U -f ../pom.xml)
fi

echo "==> PostgreSQL (Docker)"
if command -v docker >/dev/null 2>&1; then
  docker compose up -d
  echo "    Contenedor tfg-postgres en puerto 5433"
else
  echo "    AVISO: Docker no encontrado. Instala Docker Desktop para Mac."
fi

echo ""
echo "Listo. Siguiente:"
echo "  1. Backend:  cd backend && ./start-services.sh"
echo "  2. Frontend: cd frontend-react && npm run dev"
echo "  3. Abrir:    http://localhost:5173"
