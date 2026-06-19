#!/bin/bash
# Arranca los microservicios en segundo plano (quarkus:dev).
# Antes: docker compose up -d (desde la raíz del proyecto)
# Detener: ./stop-services.sh

cd "$(dirname "$0")"
chmod +x mvnw
mkdir -p logs

start() {
  name=$1
  module=$2
  port=$3
  (
    cd "$module"
    ../mvnw quarkus:dev -Dquarkus.console.enabled=false
  ) > "logs/$name.log" 2>&1 &
  echo $! > "logs/$name.pid"
  echo "$name -> puerto $port"
}

echo "Iniciando backend TFG..."
start health     health-quarkus     8081
start proyectos  proyectos-quarkus  8082
start tareas     tareas-quarkus     8083
start usuarios   usuarios-quarkus   8084
start informes   informes-quarkus   8085
sleep 3
start aggregator aggregator-quarkus 8080

echo ""
echo "API: http://localhost:8080  |  Detener: ./stop-services.sh"
