#!/bin/bash
# Detiene ./start-services.sh
ROOT="$(dirname "$0")"
kill $(cat "$ROOT/logs/"*.pid 2>/dev/null) 2>/dev/null
rm -f "$ROOT/logs/"*.pid 2>/dev/null

for port in 8080 8081 8082 8083 8084 8085; do
  pids=$(lsof -ti tcp:"$port" 2>/dev/null)
  if [ -n "$pids" ]; then
    kill $pids 2>/dev/null
  fi
done

for _ in $(seq 1 20); do
  busy=0
  for port in 8080 8081 8082 8083 8084 8085; do
    if lsof -ti tcp:"$port" >/dev/null 2>&1; then
      busy=1
      break
    fi
  done
  [ "$busy" -eq 0 ] && break
  sleep 0.25
done

echo "Servicios detenidos."
