# Casos de uso

## CU-01 Registro e inicio de sesión
**Actor:** Estudiante  
**Descripción:** Registro o login con Firebase. La app llama a `POST /api/usuarios/sync` con el token.  
**Resultado:** Sesión activa y API autorizada.

## CU-02 Gestionar proyectos
**Actor:** Estudiante autenticado  
**Descripción:** Listar, crear y editar proyectos propios en PostgreSQL.  
**Resultado:** Cada usuario ve solo sus proyectos; alta y edición operativas.

## CU-03 Ver tablero de tareas
**Actor:** Estudiante autenticado  
**Descripción:** Ver tareas por estado en el detalle del proyecto.  
**Resultado:** Datos reales; crear o mover tareas sigue en maqueta.

## CU-04 Ver equipo e invitación (maqueta)
**Actor:** Estudiante autenticado  
**Descripción:** Listado de miembros + formulario de invitar deshabilitado.  
**Resultado:** Pantalla de demostración.

## CU-05 Ver cronograma
**Actor:** Estudiante autenticado  
**Descripción:** Hitos y Gantt con datos del proyecto.  
**Resultado:** Visualización; sin editar hitos desde la app.

## CU-06 Informes
**Actor:** Estudiante autenticado  
**Descripción:** Estadísticas del microservicio informes (consulta BD por usuario).  
**Resultado:** Gráficos con datos reales del usuario.

## CU-07 Notificaciones
**Actor:** Estudiante autenticado  
**Descripción:** Lista de avisos de ejemplo.  
**Resultado:** Sin marcar como leídas en BD.
