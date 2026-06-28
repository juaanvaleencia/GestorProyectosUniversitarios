#!/bin/bash
# Arranca los microservicios en segundo plano.
# Detener: ./stop-services.sh
# Compilar antes (si hace falta): ./mvnw package -DskipTests

cd "$(dirname "$0")"
mkdir -p logs

./stop-services.sh 2>/dev/null || true

start() {
  name=$1
  module=$2
  jar=$(ls "$module"/target/*-runner.jar 2>/dev/null | head -1)
  if [ -z "$jar" ]; then
    echo "[$name] Falta el JAR. Ejecuta: ./mvnw package -DskipTests"
    return 1
  fi
  nohup java -jar "$jar" >> "logs/$name.log" 2>&1 &
  echo $! > "logs/$name.pid"
  echo "$name -> arrancando"
}

failed=0
start health     health-quarkus     || failed=1
start proyectos  proyectos-quarkus  || failed=1
start tareas     tareas-quarkus     || failed=1
start usuarios   usuarios-quarkus   || failed=1
start informes   informes-quarkus   || failed=1

sleep 12

start aggregator aggregator-quarkus || failed=1

for _ in $(seq 1 30); do
  if curl -sf http://localhost:8080/api/health >/dev/null 2>&1; then
    echo "API lista: http://localhost:8080  |  Detener: ./stop-services.sh"
    exit "$failed"
  fi
  sleep 1
done

echo "AVISO: la API no respondió. Revisa logs/*.log"
exit 1
