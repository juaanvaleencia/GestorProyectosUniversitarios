-- Esquema inicial TFG: planificación de proyectos universitarios
-- Se ejecuta automáticamente al crear el contenedor Docker (primera vez)

DROP TABLE IF EXISTS tareas CASCADE;
DROP TABLE IF EXISTS miembros_proyecto CASCADE;
DROP TABLE IF EXISTS hitos CASCADE;
DROP TABLE IF EXISTS proyectos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    firebase_uid   VARCHAR(128) PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    avatar_url     VARCHAR(500),
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE proyectos (
    id             BIGSERIAL PRIMARY KEY,
    titulo         VARCHAR(200) NOT NULL,
    descripcion    VARCHAR(2000),
    fecha_inicio   DATE,
    fecha_fin      DATE,
    estado         VARCHAR(30) NOT NULL DEFAULT 'PLANIFICACION',
    propietario_uid VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid),
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ch_proyectos_estado CHECK (estado IN ('PLANIFICACION', 'EN_CURSO', 'PAUSADO', 'FINALIZADO'))
);

CREATE TABLE miembros_proyecto (
    id             BIGSERIAL PRIMARY KEY,
    proyecto_id    BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    usuario_uid    VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid),
    rol            VARCHAR(30) NOT NULL DEFAULT 'DEVELOPER',
    CONSTRAINT uq_miembro UNIQUE (proyecto_id, usuario_uid),
    CONSTRAINT ch_rol CHECK (rol IN ('PRODUCT_OWNER', 'SCRUM_MASTER', 'DEVELOPER'))
);

CREATE TABLE tareas (
    id             BIGSERIAL PRIMARY KEY,
    proyecto_id    BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    titulo         VARCHAR(200) NOT NULL,
    descripcion    VARCHAR(1000),
    estado         VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    prioridad      VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    responsable_uid VARCHAR(128) REFERENCES usuarios(firebase_uid),
    fecha_limite   DATE,
    orden          INT NOT NULL DEFAULT 0,
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ch_tareas_estado CHECK (estado IN ('PENDIENTE', 'EN_PROGRESO', 'REVISION', 'HECHA')),
    CONSTRAINT ch_tareas_prioridad CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'URGENTE'))
);

CREATE TABLE notificaciones (
    id             BIGSERIAL PRIMARY KEY,
    usuario_uid    VARCHAR(128) NOT NULL REFERENCES usuarios(firebase_uid) ON DELETE CASCADE,
    texto          VARCHAR(500) NOT NULL,
    leida          BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hitos (
    id             BIGSERIAL PRIMARY KEY,
    proyecto_id    BIGINT NOT NULL REFERENCES proyectos(id) ON DELETE CASCADE,
    titulo         VARCHAR(200) NOT NULL,
    fecha          DATE NOT NULL,
    completado     BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO usuarios (firebase_uid, email, nombre)
VALUES
    ('demo-user-uid', 'demo@upsa.es', 'Juan Valencia'),
    ('uid-scrum-1', 'scrum@upsa.es', 'Carlos Scrum Master'),
    ('uid-dev-1', 'dev1@upsa.es', 'Laura Developer'),
    ('uid-dev-2', 'dev2@upsa.es', 'Miguel Developer');

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
