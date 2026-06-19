# Conclusiones

Con este TFG he montado un prototipo de una aplicación para planificar proyectos de la universidad. La parte que más me costó fue encajar varios microservicios Quarkus y que el frontend solo hable con el aggregator, siguiendo la estructura que vimos con `imdbFinal`.

**Lo que sí funciona:** registro/login con Firebase, validación del JWT en el backend, sincronización del usuario y consulta de proyectos/tareas de la base de datos de prueba.

**Lo que dejé a propósito sin implementar** (como indica el anteproyecto): guardar proyectos nuevos, mover tareas en el Kanban, invitaciones reales e informes calculados desde la BD. Las pantallas están para enseñar el diseño y la navegación.

Si alguien quisiera seguir el trabajo, lo lógico sería completar el CRUD de proyectos y tareas, persistir notificaciones y sacar los informes con consultas SQL reales. También vendría bien tests automáticos (por ejemplo Playwright) y desplegar en la nube, pero eso ya sería otro proyecto.
