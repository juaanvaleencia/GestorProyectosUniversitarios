#!/bin/bash
# Detiene ./start-services.sh
kill $(cat "$(dirname "$0")/logs/"*.pid 2>/dev/null) 2>/dev/null
rm -f "$(dirname "$0")/logs/"*.pid 2>/dev/null
echo "Servicios detenidos."
