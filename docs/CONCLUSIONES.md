# Conclusiones

Este TFG ha dado como resultado un **prototipo funcional** de gestión de proyectos universitarios: registro con Firebase, microservicios Quarkus con arquitectura hexagonal, aggregator como única puerta de entrada para el frontend React, y persistencia en PostgreSQL.

## Objetivos alcanzados

La aplicación cubre el ciclo de vida básico de un proyecto de equipo:

- **Autenticación y perfil:** login/registro Firebase, sincronización de usuario, asignación de universidad (UPSA/USAL) y rutas protegidas con JWT validado en el backend.
- **Proyectos:** creación libre o **desde plantillas** del catálogo académico de la universidad del alumno, con instanciación automática de tareas e hitos y asignación del tutor de la asignatura.
- **Tareas:** tablero Kanban con creación, edición, cambio de estado y eliminación.
- **Equipo:** invitación de miembros registrados por email, roles de proyecto y notificación al invitado.
- **Cronograma:** gestión de hitos y visualización Gantt junto con las tareas.
- **Informes:** estadísticas reales calculadas desde la base de datos por usuario.
- **Notificaciones:** avisos de bienvenida e invitaciones a proyectos.

La estructura del backend sigue el patrón visto en clase (`imdbFinal`): Resources en el aggregator, casos de uso por dominio, DTOs en el módulo `common` y un microservicio por responsabilidad (proyectos, tareas, usuarios, informes, health).

## Dificultades principales

1. **Orquestación de microservicios:** mantener contratos REST alineados entre aggregator y servicios, con reenvío del token de autorización.
2. **Enrutado JAX-RS:** rutas anidadas (`/api/proyectos/{id}/tareas`) requieren Resources con path explícito a nivel de clase para evitar conflictos (p. ej. 404 en operaciones POST).
3. **Catálogo e instanciación:** integrar asignaturas y plantillas en el flujo de «Nuevo proyecto» y crear en una sola operación proyecto, tareas, hitos y miembro tutor, con validación de plantilla única por usuario.
4. **Arranque local:** script `start-services.sh` con JARs empaquetados para levantar los seis servicios de forma estable en desarrollo.

## Limitaciones conocidas (alcance deliberado)

- El **catálogo académico** es de solo lectura; las plantillas se cargan por SQL, sin panel de administración.
- Los **tutores** son usuarios demo en base de datos; no existe una vista ni login específico para el rol tutor.
- Las **notificaciones** se listan pero no se marcan como leídas desde la interfaz.
- No hay tests automatizados ni despliegue en producción; es un entorno de desarrollo local (Docker + Quarkus + Vite).

## Trabajo futuro

Mejoras naturales sin cambiar la arquitectura base: marcar notificaciones como leídas, mostrar en la UI el origen del proyecto (asignatura/plantilla), notificar al tutor al instanciar una plantilla, panel de gestión del catálogo y pruebas de integración sobre el aggregator.

## Valoración personal

El proyecto demuestra que es viable aplicar una arquitectura de microservicios educativa a un caso de uso real (planificación de trabajos de asignatura), manteniendo separación de responsabilidades y una experiencia de usuario coherente a través del aggregator. La parte más valiosa del aprendizaje ha sido coordinar capas de dominio, persistencia y API en varios módulos manteniendo un único contrato hacia el frontend.
