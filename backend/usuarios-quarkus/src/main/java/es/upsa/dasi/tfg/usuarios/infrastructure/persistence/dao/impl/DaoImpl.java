package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AlumnoMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UniversidadRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DaoImpl implements Dao
{
    private final DataSource dataSource;

    @Inject
    public DaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UsuarioRow> selectUsuarioByUid(String firebaseUid) {
        final String sql = """
            SELECT firebase_uid, email, nombre, avatar_url, universidad_id, tipo
              FROM usuarios
             WHERE firebase_uid = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firebaseUid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuarioRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public UsuarioRow insertUsuario(UsuarioRow row) {
        final String sql = """
            INSERT INTO usuarios (firebase_uid, email, nombre, avatar_url, universidad_id, tipo)
            VALUES (?, ?, ?, ?, ?, COALESCE(?, 'ESTUDIANTE'))
            ON CONFLICT (firebase_uid) DO UPDATE SET
                email = EXCLUDED.email,
                nombre = EXCLUDED.nombre,
                avatar_url = EXCLUDED.avatar_url,
                universidad_id = COALESCE(EXCLUDED.universidad_id, usuarios.universidad_id)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getFirebaseUid());
            ps.setString(2, row.getEmail());
            ps.setString(3, row.getNombre());
            ps.setString(4, row.getAvatarUrl());
            if (row.getUniversidadId() != null) {
                ps.setLong(5, row.getUniversidadId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setString(6, row.getTipo() != null ? row.getTipo() : "ESTUDIANTE");
            ps.executeUpdate();
            return selectUsuarioByUid(row.getFirebaseUid()).orElse(row);
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<NotificacionRow> selectNotificacionesByUsuario(String usuarioUid) {
        final String sql = """
            SELECT n.id, n.usuario_uid, n.texto, n.leida, n.creado_en, n.tipo, n.invitacion_id, n.proyecto_id,
                   ip.estado AS invitacion_estado,
                   CASE
                     WHEN n.tipo IS DISTINCT FROM 'INVITACION_PROYECTO' OR n.invitacion_id IS NULL THEN NULL
                     WHEN ip.estado = 'ACEPTADA' THEN 'ACEPTADA'
                     WHEN ip.estado = 'RECHAZADA' THEN 'RECHAZADA'
                     WHEN EXISTS (
                       SELECT 1 FROM miembros_proyecto m
                        WHERE m.proyecto_id = n.proyecto_id AND m.usuario_uid = n.usuario_uid
                     ) OR EXISTS (
                       SELECT 1 FROM proyectos p
                        WHERE p.id = n.proyecto_id AND p.propietario_uid = n.usuario_uid
                     ) THEN 'YA_MIEMBRO'
                     WHEN EXISTS (
                       SELECT 1
                         FROM proyectos p_inv
                        WHERE p_inv.id = n.proyecto_id
                          AND p_inv.plantilla_id IS NOT NULL
                          AND (
                            EXISTS (
                              SELECT 1 FROM miembros_proyecto m
                                JOIN proyectos p ON p.id = m.proyecto_id
                               WHERE m.usuario_uid = n.usuario_uid
                                 AND p.plantilla_id = p_inv.plantilla_id
                                 AND p.id <> p_inv.id
                            )
                            OR EXISTS (
                              SELECT 1 FROM proyectos p
                               WHERE p.propietario_uid = n.usuario_uid
                                 AND p.plantilla_id = p_inv.plantilla_id
                                 AND p.id <> p_inv.id
                            )
                          )
                     ) THEN 'OTRO_GRUPO'
                     ELSE 'ACCIONABLE'
                   END AS invitacion_situacion
              FROM notificaciones n
              LEFT JOIN invitaciones_proyecto ip ON ip.id = n.invitacion_id
             WHERE n.usuario_uid = ?
             ORDER BY n.creado_en DESC
            """;
        List<NotificacionRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(NotificacionRow.builder()
                            .id(rs.getLong("id"))
                            .usuarioUid(rs.getString("usuario_uid"))
                            .texto(rs.getString("texto"))
                            .leida(rs.getBoolean("leida"))
                            .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                            .tipo(rs.getString("tipo"))
                            .invitacionId(rs.getObject("invitacion_id", Long.class))
                            .proyectoId(rs.getObject("proyecto_id", Long.class))
                            .invitacionEstado(rs.getString("invitacion_estado"))
                            .invitacionSituacion(rs.getString("invitacion_situacion"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public void insertNotificacion(String usuarioUid, String texto) {
        final String sql = """
            INSERT INTO notificaciones (usuario_uid, texto, leida) VALUES (?, ?, FALSE)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, texto);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<UniversidadRow> selectAllUniversidades() {
        final String sql = "SELECT id, codigo, nombre FROM universidades ORDER BY nombre";
        List<UniversidadRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(UniversidadRow.builder()
                        .id(rs.getLong("id"))
                        .codigo(rs.getString("codigo"))
                        .nombre(rs.getString("nombre"))
                        .build());
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public Optional<UniversidadRow> selectUniversidadById(long universidadId) {
        final String sql = "SELECT id, codigo, nombre FROM universidades WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UniversidadRow.builder()
                            .id(rs.getLong("id"))
                            .codigo(rs.getString("codigo"))
                            .nombre(rs.getString("nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public boolean existsUniversidad(long universidadId) {
        final String sql = "SELECT 1 FROM universidades WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<String> selectCodigoProfesorByUniversidadId(long universidadId) {
        final String sql = "SELECT codigo_profesor FROM universidades WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("codigo_profesor"));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<AsignaturaMatriculaRow> selectAsignaturasImpartidasByProfesor(String usuarioUid) {
        final String sql = """
            SELECT a.id, a.universidad_id, a.nombre, a.descripcion, a.tutor_nombre
              FROM profesor_asignaturas pa
              JOIN asignaturas a ON a.id = pa.asignatura_id
             WHERE pa.usuario_uid = ?
             ORDER BY a.nombre
            """;
        List<AsignaturaMatriculaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAsignaturaMatriculaRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public void replaceProfesorAsignaturas(String usuarioUid, List<Long> asignaturaIds) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement delete = c.prepareStatement(
                        "DELETE FROM profesor_asignaturas WHERE usuario_uid = ?")) {
                    delete.setString(1, usuarioUid);
                    delete.executeUpdate();
                }
                if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
                    try (PreparedStatement insert = c.prepareStatement(
                            "INSERT INTO profesor_asignaturas (usuario_uid, asignatura_id) VALUES (?, ?)")) {
                        for (Long asignaturaId : asignaturaIds) {
                            insert.setString(1, usuarioUid);
                            insert.setLong(2, asignaturaId);
                            insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void updateAsignaturaTutor(long asignaturaId, String tutorUid, String tutorNombre) {
        final String sql = """
            UPDATE asignaturas SET tutor_demo_uid = ?, tutor_nombre = ? WHERE id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tutorUid);
            ps.setString(2, tutorNombre);
            ps.setLong(3, asignaturaId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void clearAsignaturaTutor(long asignaturaId) {
        final String sql = """
            UPDATE asignaturas SET tutor_demo_uid = NULL, tutor_nombre = NULL WHERE id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, asignaturaId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<AsignaturaMatriculaRow> selectAsignaturasMatriculadasByUsuario(String usuarioUid) {
        final String sql = """
            SELECT a.id, a.universidad_id, a.nombre, a.descripcion, a.tutor_nombre
              FROM usuario_asignaturas ua
              JOIN asignaturas a ON a.id = ua.asignatura_id
             WHERE ua.usuario_uid = ?
             ORDER BY a.nombre
            """;
        List<AsignaturaMatriculaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAsignaturaMatriculaRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public void replaceAsignaturasMatriculadas(String usuarioUid, List<Long> asignaturaIds) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement delete = c.prepareStatement(
                        "DELETE FROM usuario_asignaturas WHERE usuario_uid = ?")) {
                    delete.setString(1, usuarioUid);
                    delete.executeUpdate();
                }
                if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
                    try (PreparedStatement insert = c.prepareStatement(
                            "INSERT INTO usuario_asignaturas (usuario_uid, asignatura_id) VALUES (?, ?)")) {
                        for (Long asignaturaId : asignaturaIds) {
                            insert.setString(1, usuarioUid);
                            insert.setLong(2, asignaturaId);
                            insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean asignaturasPertenecenAUniversidad(List<Long> asignaturaIds, long universidadId) {
        if (asignaturaIds == null || asignaturaIds.isEmpty()) {
            return false;
        }
        final String sql = """
            SELECT COUNT(*) FROM asignaturas
             WHERE universidad_id = ? AND id = ANY(?)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            Array array = c.createArrayOf("BIGINT", asignaturaIds.toArray());
            ps.setArray(2, array);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == asignaturaIds.size();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<String> findNombresAsignaturasOcupadasPorOtrosProfesores(List<Long> asignaturaIds, String profesorUid) {
        if (asignaturaIds == null || asignaturaIds.isEmpty()) {
            return List.of();
        }
        final String sql = """
            SELECT a.nombre
              FROM profesor_asignaturas pa
              JOIN asignaturas a ON a.id = pa.asignatura_id
             WHERE pa.usuario_uid <> ?
               AND pa.asignatura_id = ANY(?)
             ORDER BY a.nombre
            """;
        List<String> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, profesorUid);
            Array array = c.createArrayOf("BIGINT", asignaturaIds.toArray());
            ps.setArray(2, array);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<AlumnoMatriculaRow> selectAlumnosMatriculadosByProfesor(String profesorUid) {
        final String sql = """
            SELECT u.firebase_uid, u.nombre, u.email,
                   a.id AS asignatura_id, a.nombre AS asignatura_nombre,
                   a.universidad_id, a.descripcion, a.tutor_nombre
              FROM usuario_asignaturas ua
              JOIN usuarios u ON u.firebase_uid = ua.usuario_uid
              JOIN asignaturas a ON a.id = ua.asignatura_id
             WHERE ua.asignatura_id IN (
                   SELECT asignatura_id FROM profesor_asignaturas WHERE usuario_uid = ?
             )
               AND COALESCE(u.tipo, 'ESTUDIANTE') = 'ESTUDIANTE'
             ORDER BY u.nombre, a.nombre
            """;
        List<AlumnoMatriculaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, profesorUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(AlumnoMatriculaRow.builder()
                            .firebaseUid(rs.getString("firebase_uid"))
                            .nombre(rs.getString("nombre"))
                            .email(rs.getString("email"))
                            .asignaturaId(rs.getLong("asignatura_id"))
                            .asignaturaNombre(rs.getString("asignatura_nombre"))
                            .universidadId(rs.getLong("universidad_id"))
                            .asignaturaDescripcion(rs.getString("descripcion"))
                            .tutorNombre(rs.getString("tutor_nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public boolean profesorSupervisaAlumno(String profesorUid, String alumnoUid) {
        final String sql = """
            SELECT COUNT(*) > 0
              FROM usuario_asignaturas ua
              JOIN profesor_asignaturas pa ON pa.asignatura_id = ua.asignatura_id
             WHERE ua.usuario_uid = ? AND pa.usuario_uid = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, alumnoUid);
            ps.setString(2, profesorUid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    private AsignaturaMatriculaRow mapAsignaturaMatriculaRow(ResultSet rs) throws SQLException {
        return AsignaturaMatriculaRow.builder()
                .id(rs.getLong("id"))
                .universidadId(rs.getLong("universidad_id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .tutorNombre(rs.getString("tutor_nombre"))
                .build();
    }

    private UsuarioRow mapUsuarioRow(ResultSet rs) throws SQLException {
        return UsuarioRow.builder()
                .firebaseUid(rs.getString("firebase_uid"))
                .email(rs.getString("email"))
                .nombre(rs.getString("nombre"))
                .avatarUrl(rs.getString("avatar_url"))
                .universidadId(rs.getObject("universidad_id", Long.class))
                .tipo(rs.getString("tipo"))
                .build();
    }

    @Override
    public AsignaturaMatriculaRow createAsignaturaParaProfesor(
            String usuarioUid, long universidadId, String nombre, String descripcion, String tutorNombre)
    {
        final String insertSql = """
            INSERT INTO asignaturas (universidad_id, nombre, descripcion, tutor_nombre, tutor_demo_uid)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, universidad_id, nombre, descripcion, tutor_nombre
            """;
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                long asignaturaId;
                AsignaturaMatriculaRow row;
                try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                    ps.setLong(1, universidadId);
                    ps.setString(2, nombre);
                    ps.setString(3, descripcion);
                    ps.setString(4, tutorNombre);
                    ps.setString(5, usuarioUid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new TfgRuntimeException("No se pudo crear la asignatura");
                        }
                        asignaturaId = rs.getLong("id");
                        row = mapAsignaturaMatriculaRow(rs);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO profesor_asignaturas (usuario_uid, asignatura_id) VALUES (?, ?)")) {
                    ps.setString(1, usuarioUid);
                    ps.setLong(2, asignaturaId);
                    ps.executeUpdate();
                }
                c.commit();
                return row;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
    }
}
