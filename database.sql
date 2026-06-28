-- Esquema TFG: planificación de proyectos universitarios (fases 0–10 consolidadas)
-- Se ejecuta automáticamente al crear el contenedor Docker (primera vez).
-- Para recrear desde cero: docker compose down -v && docker compose up -d

DROP TABLE IF EXISTS tareas CASCADE;
DROP TABLE IF EXISTS notificaciones CASCADE;
DROP TABLE IF EXISTS invitaciones_proyecto CASCADE;
DROP TABLE IF EXISTS miembros_proyecto CASCADE;
DROP TABLE IF EXISTS hitos CASCADE;
DROP TABLE IF EXISTS proyectos CASCADE;
DROP TABLE IF EXISTS usuario_asignaturas CASCADE;
DROP TABLE IF EXISTS profesor_asignaturas CASCADE;
DROP TABLE IF EXISTS plantillas_hito CASCADE;
DROP TABLE IF EXISTS plantillas_tarea CASCADE;
DROP TABLE IF EXISTS plantillas_proyecto CASCADE;
DROP TABLE IF EXISTS asignaturas CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS universidades CASCADE;

CREATE TABLE universidades (
    id              BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(20) NOT NULL UNIQUE,
    nombre          VARCHAR(200) NOT NULL,
    codigo_profesor VARCHAR(100)
);

CREATE TABLE usuarios (
    firebase_uid   VARCHAR(128) PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    avatar_url     VARCHAR(500),
    universidad_id BIGINT REFERENCES universidades(id),
    tipo           VARCHAR(20) NOT NULL DEFAULT 'ESTUDIANTE',
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ch_usuario_tipo CHECK (tipo IN ('ESTUDIANTE', 'PROFESOR'))
);

INSERT INTO universidades (codigo, nombre, codigo_profesor)
VALUES
    ('UPSA', 'Universidad Pontificia de Salamanca', 'upsa-prof-dev'),
    ('USAL', 'Universidad de Salamanca', 'usal-prof-dev');

INSERT INTO usuarios (firebase_uid, email, nombre, universidad_id, tipo) VALUES
    ('tutor-bbdd2', 'roberto.berjon@upsa.es', 'Roberto Berjón', 1, 'PROFESOR'),
    ('tutor-isw', 'ana.fermoso@upsa.es', 'Ana Fermoso', 1, 'PROFESOR'),
    ('tutor-inf-teorica', 'pedro.perez@usal.es', 'Pedro Pérez', 2, 'PROFESOR'),
    ('tutor-fisica', 'paco.rodriguez@usal.es', 'Paco Rodríguez', 2, 'PROFESOR');

CREATE TABLE asignaturas (
    id               BIGSERIAL PRIMARY KEY,
    universidad_id   BIGINT NOT NULL REFERENCES universidades(id),
    nombre           VARCHAR(200) NOT NULL,
    descripcion      VARCHAR(1000),
    tutor_nombre     VARCHAR(150),
    tutor_demo_uid   VARCHAR(128)
);

CREATE TABLE plantillas_proyecto (
    id                     BIGSERIAL PRIMARY KEY,
    asignatura_id          BIGINT NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    titulo                 VARCHAR(200) NOT NULL,
    descripcion            VARCHAR(2000),
    orden                  INT NOT NULL DEFAULT 0,
    fecha_inicio_sugerida  DATE,
    fecha_fin_sugerida     DATE
);

CREATE TABLE plantillas_tarea (
    id                     BIGSERIAL PRIMARY KEY,
    plantilla_proyecto_id  BIGINT NOT NULL REFERENCES plantillas_proyecto(id) ON DELETE CASCADE,
    titulo                 VARCHAR(200) NOT NULL,
    descripcion            VARCHAR(1000),
    orden                  INT NOT NULL DEFAULT 0,
    fecha_limite_sugerida  DATE
);

CREATE TABLE plantillas_hito (
    id                     BIGSERIAL PRIMARY KEY,
    plantilla_proyecto_id  BIGINT NOT NULL REFERENCES plantillas_proyecto(id) ON DELETE CASCADE,
    titulo                 VARCHAR(200) NOT NULL,
    fecha_sugerida         DATE NOT NULL,
    orden                  INT NOT NULL DEFAULT 0
);

-- Catálogo académico (UPSA id=1, USAL id=2). Tutor NULL hasta que un profesor declare la asignatura.
INSERT INTO asignaturas (universidad_id, nombre, descripcion, tutor_nombre, tutor_demo_uid) VALUES
(1, 'Bases de datos 2', 'Diseño e implementación de bases de datos relacionales', NULL, NULL),
(1, 'Ingeniería del software web', 'Desarrollo de aplicaciones web con arquitectura en capas', NULL, NULL),
(1, 'Desarrollo y administración de sistemas de información', 'Análisis, diseño e implantación de sistemas de información', NULL, NULL),
(1, 'Programación de aplicaciones', 'Desarrollo de aplicaciones con buenas prácticas y arquitectura', NULL, NULL),
(1, 'Algoritmos y estructuras de datos', 'Complejidad, estructuras fundamentales y resolución de problemas', NULL, NULL),
(1, 'Fundamentos de la interacción persona-ordenador', 'Usabilidad, accesibilidad y diseño de interfaces', NULL, NULL),
(1, 'Estadística', 'Estadística descriptiva e inferencial aplicada a la informática', NULL, NULL),
(1, 'Informática teórica', 'Autómatas, lenguajes formales y fundamentos de la computación', NULL, NULL),
(1, 'Inteligencia artificial', 'Técnicas de IA, aprendizaje automático y razonamiento', NULL, NULL),
(2, 'Informática Teórica', 'Autómatas, lenguajes formales y complejidad computacional', NULL, NULL),
(2, 'Física', 'Fundamentos de física con componente teórico y de laboratorio', NULL, NULL);

INSERT INTO plantillas_proyecto (asignatura_id, titulo, descripcion, orden, fecha_inicio_sugerida, fecha_fin_sugerida) VALUES
(1, 'Modelado conceptual', 'Diseño del modelo de datos del caso de estudio', 1, '2026-02-15', '2026-03-30'),
(1, 'Implementación SQL', 'Creación del esquema físico y consultas', 2, '2026-04-01', '2026-05-15'),
(1, 'Proyecto integrador BD', 'Integración completa con documentación y defensa', 3, '2026-05-16', '2026-06-30'),
(2, 'Análisis y diseño', 'Especificación funcional y arquitectura', 1, '2026-02-01', '2026-03-15'),
(2, 'Desarrollo frontend', 'Interfaz de usuario con React', 2, '2026-03-16', '2026-04-30'),
(2, 'Desarrollo backend', 'API REST y persistencia', 3, '2026-03-16', '2026-05-15'),
(2, 'Entrega final', 'Pruebas, documentación y despliegue', 4, '2026-05-16', '2026-06-30'),
(10, 'Autómatas y lenguajes', 'Implementación y análisis de autómatas finitos', 1, '2026-02-10', '2026-04-15'),
(10, 'Complejidad computacional', 'Estudio de un problema NP y su reducción', 2, '2026-04-16', '2026-06-15'),
(11, 'Laboratorio: cinemática', 'Experimento y análisis de movimiento rectilíneo', 1, '2026-02-01', '2026-03-31'),
(11, 'Trabajo teórico: campos', 'Estudio de campos gravitatorio y eléctrico', 2, '2026-04-01', '2026-06-01');

INSERT INTO plantillas_tarea (plantilla_proyecto_id, titulo, descripcion, orden, fecha_limite_sugerida) VALUES
(1, 'Análisis de requisitos', 'Identificar entidades y relaciones del dominio', 1, '2026-02-28'),
(1, 'Diagrama entidad-relación', 'Modelo ER completo con cardinalidades', 2, '2026-03-10'),
(1, 'Normalización', 'Aplicar 1FN, 2FN y 3FN al modelo', 3, '2026-03-20'),
(1, 'Diccionario de datos', 'Documentar atributos, tipos y restricciones', 4, '2026-03-28'),
(2, 'Script DDL', 'Crear tablas, claves y restricciones', 1, '2026-04-15'),
(2, 'Índices y vistas', 'Optimizar consultas frecuentes', 2, '2026-04-25'),
(2, 'Datos de prueba', 'Poblar la BD con casos representativos', 3, '2026-05-05'),
(2, 'Consultas SQL avanzadas', 'JOINs, subconsultas y agregaciones', 4, '2026-05-12'),
(3, 'Procedimientos almacenados', 'Lógica de negocio en la BD', 1, '2026-05-25'),
(3, 'Informe técnico', 'Memoria del proyecto con diagramas', 2, '2026-06-15'),
(3, 'Presentación oral', 'Defensa ante el profesor', 3, '2026-06-28'),
(4, 'Casos de uso', 'Diagramas y descripción de actores', 1, '2026-02-20'),
(4, 'Wireframes', 'Bocetos de pantallas principales', 2, '2026-03-01'),
(4, 'Diseño de API REST', 'Endpoints, DTOs y contratos', 3, '2026-03-10'),
(5, 'Setup React + Vite', 'Estructura del proyecto frontend', 1, '2026-03-25'),
(5, 'Componentes principales', 'Layout, formularios y listados', 2, '2026-04-10'),
(5, 'Integración con API', 'Consumo de endpoints con fetch', 3, '2026-04-25'),
(6, 'Modelo de dominio', 'Entidades y casos de uso', 1, '2026-04-01'),
(6, 'Endpoints CRUD', 'Operaciones sobre recursos', 2, '2026-04-20'),
(6, 'Autenticación JWT', 'Integración con Firebase/OIDC', 3, '2026-05-05'),
(7, 'Pruebas de integración', 'Verificar flujos completos', 1, '2026-06-01'),
(7, 'Documentación API', 'OpenAPI o manual de endpoints', 2, '2026-06-15'),
(7, 'Demo y memoria', 'Presentación del proyecto', 3, '2026-06-28'),
(8, 'Definición formal', 'Especificar el autómata y su lenguaje', 1, '2026-02-28'),
(8, 'Implementación', 'Simulador del autómata en código', 2, '2026-03-20'),
(8, 'Análisis de propiedades', 'Demostrar cerradura y decisiones', 3, '2026-04-05'),
(8, 'Informe escrito', 'Documento con demostraciones', 4, '2026-04-12'),
(9, 'Elección del problema', 'Seleccionar problema NP-completo', 1, '2026-04-25'),
(9, 'Reducción', 'Construir reducción desde SAT u otro problema', 2, '2026-05-20'),
(9, 'Presentación', 'Exposición en clase', 3, '2026-06-10'),
(10, 'Protocolo experimental', 'Diseño del montaje y variables', 1, '2026-02-15'),
(10, 'Toma de datos', 'Registro de mediciones en laboratorio', 2, '2026-02-28'),
(10, 'Análisis gráfico', 'Representar posición, velocidad y aceleración', 3, '2026-03-15'),
(10, 'Conclusiones', 'Interpretar resultados y errores', 4, '2026-03-28'),
(11, 'Bibliografía', 'Fuentes sobre teoría de campos', 1, '2026-04-15'),
(11, 'Desarrollo teórico', 'Explicación con ecuaciones y ejemplos', 2, '2026-05-01'),
(11, 'Ejercicios resueltos', 'Problemas tipo examen', 3, '2026-05-20'),
(11, 'Entrega escrita', 'Documento final del trabajo', 4, '2026-05-28');

INSERT INTO plantillas_hito (plantilla_proyecto_id, titulo, fecha_sugerida, orden) VALUES
(1, 'Entrega modelo ER', '2026-03-15', 1),
(1, 'Revisión con tutor', '2026-03-28', 2),
(2, 'BD desplegada en PostgreSQL', '2026-05-01', 1),
(2, 'Demo de consultas', '2026-05-15', 2),
(3, 'Entrega final', '2026-06-30', 1),
(4, 'Diseño aprobado', '2026-03-15', 1),
(5, 'Frontend funcional', '2026-04-30', 1),
(6, 'API operativa', '2026-05-15', 1),
(7, 'Entrega y defensa', '2026-06-30', 1),
(8, 'Entrega intermedia', '2026-03-15', 1),
(8, 'Defensa proyecto 1', '2026-04-15', 2),
(9, 'Entrega final teoría', '2026-06-15', 1),
(10, 'Práctica de laboratorio', '2026-03-01', 1),
(10, 'Memoria de prácticas', '2026-03-31', 2),
(11, 'Entrega trabajo teórico', '2026-06-01', 1);

CREATE TABLE proyectos (
    id              BIGSERIAL PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    descripcion     VARCHAR(2000),
    fecha_inicio    DATE,
    fecha_fin       DATE,
    estado          VARCHAR(30) NOT NULL DEFAULT 'PLANIFICACION',
    propietario_uid VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid),
    plantilla_id    BIGINT REFERENCES plantillas_proyecto(id),
    asignatura_id   BIGINT REFERENCES asignaturas(id),
    creado_en       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ch_proyectos_estado CHECK (estado IN ('PLANIFICACION', 'EN_CURSO', 'PAUSADO', 'FINALIZADO'))
);

CREATE UNIQUE INDEX uq_proyecto_usuario_plantilla
    ON proyectos (propietario_uid, plantilla_id)
    WHERE plantilla_id IS NOT NULL;

CREATE TABLE miembros_proyecto (
    id          BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    usuario_uid VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid),
    rol         VARCHAR(30) NOT NULL DEFAULT 'DEVELOPER',
    CONSTRAINT uq_miembro UNIQUE (proyecto_id, usuario_uid),
    CONSTRAINT ch_rol CHECK (rol IN ('PRODUCT_OWNER', 'SCRUM_MASTER', 'DEVELOPER', 'TUTOR'))
);

CREATE TABLE tareas (
    id              BIGSERIAL PRIMARY KEY,
    proyecto_id     BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    titulo          VARCHAR(200) NOT NULL,
    descripcion     VARCHAR(1000),
    estado          VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    prioridad       VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    responsable_uid VARCHAR(128) REFERENCES usuarios(firebase_uid),
    fecha_limite    DATE,
    orden           INT NOT NULL DEFAULT 0,
    origen          VARCHAR(20) NOT NULL DEFAULT 'ALUMNO',
    tarea_padre_id  BIGINT REFERENCES tareas(id) ON DELETE CASCADE,
    letra_subtarea  VARCHAR(2),
    creado_en       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ch_tareas_estado CHECK (estado IN ('PENDIENTE', 'EN_PROGRESO', 'REVISION', 'HECHA')),
    CONSTRAINT ch_tareas_prioridad CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'URGENTE')),
    CONSTRAINT ch_tareas_origen CHECK (origen IN ('PROFESOR', 'ALUMNO'))
);

CREATE TABLE hitos (
    id          BIGSERIAL PRIMARY KEY,
    proyecto_id BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    titulo      VARCHAR(200) NOT NULL,
    fecha       DATE NOT NULL,
    completado  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE invitaciones_proyecto (
    id                   BIGSERIAL PRIMARY KEY,
    proyecto_id          BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    usuario_invitado_uid VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid) ON DELETE CASCADE,
    rol                  VARCHAR(32) NOT NULL,
    estado               VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    invitado_por_uid     VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid),
    creado_en            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ch_invitacion_estado CHECK (estado IN ('PENDIENTE', 'ACEPTADA', 'RECHAZADA'))
);

CREATE UNIQUE INDEX uq_invitacion_pendiente
    ON invitaciones_proyecto (proyecto_id, usuario_invitado_uid)
    WHERE estado = 'PENDIENTE';

CREATE TABLE notificaciones (
    id            BIGSERIAL PRIMARY KEY,
    usuario_uid   VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid) ON DELETE CASCADE,
    texto         VARCHAR(500) NOT NULL,
    leida         BOOLEAN NOT NULL DEFAULT FALSE,
    tipo          VARCHAR(50),
    invitacion_id BIGINT REFERENCES invitaciones_proyecto(id) ON DELETE SET NULL,
    proyecto_id   BIGINT REFERENCES proyectos(id) ON DELETE SET NULL,
    creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuario_asignaturas (
    usuario_uid   VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid) ON DELETE CASCADE,
    asignatura_id BIGINT NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    creado_en     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_uid, asignatura_id)
);

CREATE TABLE profesor_asignaturas (
    usuario_uid   VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid) ON DELETE CASCADE,
    asignatura_id BIGINT NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    creado_en     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_uid, asignatura_id)
);

INSERT INTO usuarios (firebase_uid, email, nombre, universidad_id, tipo)
VALUES
    ('demo-user-uid', 'demo@upsa.es', 'Juan Valencia', 1, 'ESTUDIANTE'),
    ('uid-scrum-1', 'scrum@upsa.es', 'Carlos Scrum Master', 1, 'ESTUDIANTE'),
    ('uid-dev-1', 'dev1@upsa.es', 'Laura Developer', 1, 'ESTUDIANTE'),
    ('uid-dev-2', 'dev2@upsa.es', 'Miguel Developer', 1, 'ESTUDIANTE');

INSERT INTO proyectos (titulo, descripcion, fecha_inicio, fecha_fin, estado, propietario_uid)
VALUES
    ('TFG - Plataforma de proyectos', 'Desarrollo del sistema web para planificación universitaria', '2026-01-15', '2026-06-30', 'EN_CURSO', 'demo-user-uid'),
    ('Prácticas Empresa', 'Memoria y entregables del periodo de prácticas', '2026-03-01', '2026-05-15', 'PLANIFICACION', 'demo-user-uid');

INSERT INTO miembros_proyecto (proyecto_id, usuario_uid, rol)
VALUES
    (1, 'demo-user-uid', 'PRODUCT_OWNER'),
    (1, 'uid-scrum-1', 'SCRUM_MASTER'),
    (1, 'uid-dev-1', 'DEVELOPER'),
    (1, 'uid-dev-2', 'DEVELOPER'),
    (2, 'demo-user-uid', 'PRODUCT_OWNER'),
    (2, 'uid-dev-1', 'DEVELOPER');

INSERT INTO tareas (proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid, fecha_limite, orden)
VALUES
    (1, 'Diseñar base de datos', 'Modelo ER y script SQL', 'HECHA', 'ALTA', 'demo-user-uid', '2026-02-01', 1),
    (1, 'Backend Quarkus', 'API REST proyectos y tareas', 'EN_PROGRESO', 'ALTA', 'demo-user-uid', '2026-03-15', 2),
    (1, 'Frontend React', 'Pantallas y login Firebase', 'PENDIENTE', 'MEDIA', 'demo-user-uid', '2026-04-01', 3),
    (1, 'Integración OIDC', 'Validación JWT con Firebase', 'PENDIENTE', 'ALTA', NULL, '2026-04-20', 4);

INSERT INTO hitos (proyecto_id, titulo, fecha, completado)
VALUES
    (1, 'Anteproyecto aprobado', '2026-01-20', TRUE),
    (1, 'Prototipo funcional', '2026-04-30', FALSE),
    (1, 'Entrega memoria TFG', '2026-06-15', FALSE);

INSERT INTO notificaciones (usuario_uid, texto, leida)
VALUES
    ('demo-user-uid', 'Bienvenido a la plataforma de proyectos universitarios.', FALSE),
    ('demo-user-uid', 'Tienes tareas pendientes en «TFG - Plataforma de proyectos».', FALSE),
    ('uid-dev-1', 'Has sido añadido al proyecto «TFG - Plataforma de proyectos».', TRUE);
