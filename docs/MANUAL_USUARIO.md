# Manual de usuario

## Qué puedes hacer

Es una **versión de prueba** del sistema: puedes entrar con tu cuenta, ver proyectos de ejemplo y recorrer todas las pantallas. La mayoría de botones de guardar o editar no hacen nada todavía.

## Entrar en la aplicación

1. Abre http://localhost:5173 (con el backend y `npm run dev` en marcha).
2. Regístrate o inicia sesión con email y contraseña (Firebase).
3. Verás el **Inicio** con un resumen de proyectos.

## Menú

- **Inicio:** resumen y estado de la API.
- **Proyectos:** listado; al pulsar uno ves tareas, cronograma y equipo.
- **Informes:** gráficos con números de ejemplo.
- **Notificaciones:** avisos de muestra.
- **Perfil:** tu email y datos de sesión.
- **Cerrar sesión:** abajo en la barra lateral.

## Proyectos

En el detalle hay tres pestañas:

- **Tareas:** tablero tipo Kanban (solo lectura).
- **Cronograma:** hitos y diagrama Gantt.
- **Equipo:** miembros del proyecto; el formulario de invitar no envía nada.

Los enlaces “Maqueta: nuevo/editar” muestran cómo serían los formularios sin guardar en base de datos.

## Si algo no carga

Comprueba que Docker (PostgreSQL) esté activo y que hayas ejecutado `./start-services.sh` dentro de `backend`. Ver [README](../README.md).
