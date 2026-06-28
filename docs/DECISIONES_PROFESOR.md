# Decisiones cerradas con el profesor

Documento de referencia para el desarrollo del TFG.

## Autenticación y registro

| Tema | Decisión |
|------|----------|
| Código de profesor | **Uno por universidad** (`universidades.codigo_profesor`) |
| Verificación profesor | Cuenta **Firebase** + código universidad en registro (fase 3) |
| Email al registrar | UPSA → `@upsa.es`; USAL → `@usal.es` |
| Matrículas | Se eligen **al registrarse** (máx. 10); **editables en perfil** |

## Tareas y subtareas (fases posteriores)

| Tema | Decisión |
|------|----------|
| Subtareas en informes/progreso | Cuentan como **una sola tarea** del profesor |
| Subtareas en UI | Distinción **visual** en tablero y cronograma |
| Responsable subtarea | Cada subtarea puede tener **responsable distinto** |

## Equipo e invitaciones (fases posteriores)

| Tema | Decisión |
|------|----------|
| Invitaciones | Solo a usuarios **ya registrados** |
| Elegibilidad | Debe estar **matriculado en la asignatura** del proyecto |
| Duplicados | No si ya está en el proyecto (cualquier rol) |

## Roles de aplicación

- `ESTUDIANTE`: flujo actual (proyectos, informes, notificaciones).
- `PROFESOR`: portal propio sin informes/notificaciones (fase 4+).
