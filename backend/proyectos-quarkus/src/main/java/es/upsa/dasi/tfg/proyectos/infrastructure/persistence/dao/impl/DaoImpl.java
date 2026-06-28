package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.AsignaturaRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaHitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaTareaRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.hito.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.InvitacionProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.GrupoSupervisionMemberRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.UsuarioRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<ProyectoRow> selectProyectosByUsuario(String usuarioUid) {
        final String sql = """
            SELECT DISTINCT p.id, p.titulo, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado,
                   p.propietario_uid, p.plantilla_id, p.asignatura_id, a.nombre AS asignatura_nombre,
                   p.creado_en, p.actualizado_en
              FROM proyectos p
              LEFT JOIN asignaturas a ON a.id = p.asignatura_id
              LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
             WHERE p.propietario_uid = ? OR m.usuario_uid = ?
             ORDER BY p.actualizado_en DESC
            """;
        return queryProyectos(sql, ps -> {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
        });
    }

    @Override
    public Optional<ProyectoRow> selectProyectoById(long id) {
        final String sql = """
            SELECT p.id, p.titulo, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado, p.propietario_uid,
                   p.plantilla_id, p.asignatura_id, a.nombre AS asignatura_nombre, p.creado_en, p.actualizado_en
              FROM proyectos p
              LEFT JOIN asignaturas a ON a.id = p.asignatura_id
             WHERE p.id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapProyectoRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public ProyectoRow insertProyecto(ProyectoRow row) {
        final String sql = """
            INSERT INTO proyectos (titulo, descripcion, fecha_inicio, fecha_fin, estado, propietario_uid, plantilla_id, asignatura_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, creado_en, actualizado_en
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getTitulo());
            ps.setString(2, row.getDescripcion());
            setDate(ps, 3, row.getFechaInicio());
            setDate(ps, 4, row.getFechaFin());
            ps.setString(5, row.getEstado());
            ps.setString(6, row.getPropietarioUid());
            if (row.getPlantillaId() == null) ps.setNull(7, Types.BIGINT);
            else ps.setLong(7, row.getPlantillaId());
            if (row.getAsignaturaId() == null) ps.setNull(8, Types.BIGINT);
            else ps.setLong(8, row.getAsignaturaId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return row.withId(rs.getLong("id"))
                        .withCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                        .withActualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime());
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<ProyectoRow> updateProyecto(ProyectoRow row) {
        final String sql = """
            UPDATE proyectos SET titulo=?, descripcion=?, fecha_inicio=?, fecha_fin=?, estado=?, actualizado_en=CURRENT_TIMESTAMP
             WHERE id=? RETURNING actualizado_en
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getTitulo());
            ps.setString(2, row.getDescripcion());
            setDate(ps, 3, row.getFechaInicio());
            setDate(ps, 4, row.getFechaFin());
            ps.setString(5, row.getEstado());
            ps.setLong(6, row.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(row.withActualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime()));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int deleteProyectoById(long id)
    {
        final String sql = "DELETE FROM proyectos WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<HitoRow> selectHitosByProyecto(long proyectoId) {
        final String sql = "SELECT id, proyecto_id, titulo, fecha, completado FROM hitos WHERE proyecto_id = ? ORDER BY fecha";
        List<HitoRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHitoRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public Optional<HitoRow> selectHitoById(long proyectoId, long hitoId) {
        final String sql = "SELECT id, proyecto_id, titulo, fecha, completado FROM hitos WHERE proyecto_id = ? AND id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setLong(2, hitoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapHitoRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public HitoRow insertHito(HitoRow row) {
        final String sql = """
            INSERT INTO hitos (proyecto_id, titulo, fecha, completado)
            VALUES (?, ?, ?, ?) RETURNING id
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, row.getProyectoId());
            ps.setString(2, row.getTitulo());
            setDate(ps, 3, row.getFecha());
            ps.setBoolean(4, row.isCompletado());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return row.withId(rs.getLong("id"));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<HitoRow> updateHito(HitoRow row) {
        final String sql = """
            UPDATE hitos SET titulo=?, fecha=?, completado=?
             WHERE id=? AND proyecto_id=? RETURNING id
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getTitulo());
            setDate(ps, 2, row.getFecha());
            ps.setBoolean(3, row.isCompletado());
            ps.setLong(4, row.getId());
            ps.setLong(5, row.getProyectoId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(row);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int deleteHitoById(long proyectoId, long hitoId) {
        final String sql = "DELETE FROM hitos WHERE proyecto_id = ? AND id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setLong(2, hitoId);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<ParticipacionRow> selectParticipacionesByUsuario(String usuarioUid) {
        final String sql = """
            SELECT p.id, p.titulo, p.estado, m.rol
              FROM miembros_proyecto m
              JOIN proyectos p ON p.id = m.proyecto_id
             WHERE m.usuario_uid = ?
             ORDER BY p.titulo
            """;
        List<ParticipacionRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(ParticipacionRow.builder()
                            .proyectoId(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .estado(rs.getString("estado"))
                            .rol(rs.getString("rol"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<ParticipacionRow> selectParticipacionesAlumnoForProfesor(String profesorUid, String alumnoUid) {
        final String sql = """
            SELECT DISTINCT p.id, p.titulo, p.estado, m.rol
              FROM miembros_proyecto m
              JOIN proyectos p ON p.id = m.proyecto_id
              JOIN profesor_asignaturas pa ON pa.asignatura_id = p.asignatura_id
             WHERE m.usuario_uid = ?
               AND pa.usuario_uid = ?
               AND p.asignatura_id IS NOT NULL
             ORDER BY p.titulo
            """;
        List<ParticipacionRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, alumnoUid);
            ps.setString(2, profesorUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(ParticipacionRow.builder()
                            .proyectoId(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .estado(rs.getString("estado"))
                            .rol(rs.getString("rol"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<MiembroRow> selectMiembrosByProyecto(long proyectoId) {
        final String sql = """
            SELECT m.id, m.proyecto_id, m.usuario_uid, m.rol, u.email, u.nombre
              FROM miembros_proyecto m JOIN usuarios u ON u.firebase_uid = m.usuario_uid
             WHERE m.proyecto_id = ?
             ORDER BY m.id
            """;
        List<MiembroRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMiembroRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public Optional<MiembroRow> selectMiembroById(long proyectoId, long miembroId) {
        final String sql = """
            SELECT m.id, m.proyecto_id, m.usuario_uid, m.rol, u.email, u.nombre
              FROM miembros_proyecto m JOIN usuarios u ON u.firebase_uid = m.usuario_uid
             WHERE m.proyecto_id = ? AND m.id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setLong(2, miembroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMiembroRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<MiembroRow> selectMiembroByUsuarioUid(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT m.id, m.proyecto_id, m.usuario_uid, m.rol, u.email, u.nombre
              FROM miembros_proyecto m JOIN usuarios u ON u.firebase_uid = m.usuario_uid
             WHERE m.proyecto_id = ? AND m.usuario_uid = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMiembroRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioRow> selectUsuarioByEmail(String email) {
        final String sql = """
            SELECT firebase_uid, email, nombre
              FROM usuarios
             WHERE LOWER(email) = LOWER(?)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UsuarioRow.builder()
                            .firebaseUid(rs.getString("firebase_uid"))
                            .email(rs.getString("email"))
                            .nombre(rs.getString("nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioRow> selectUsuarioByUid(String usuarioUid) {
        final String sql = """
            SELECT firebase_uid, email, nombre
              FROM usuarios
             WHERE firebase_uid = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UsuarioRow.builder()
                            .firebaseUid(rs.getString("firebase_uid"))
                            .email(rs.getString("email"))
                            .nombre(rs.getString("nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public boolean existsMiembro(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT 1 FROM proyectos p
             LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
             WHERE p.id = ? AND (p.propietario_uid = ? OR m.usuario_uid = ?)
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            ps.setString(3, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isProductOwner(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT 1 FROM proyectos p
             WHERE p.id = ? AND p.propietario_uid = ?
            UNION ALL
            SELECT 1 FROM miembros_proyecto m
             WHERE m.proyecto_id = ? AND m.usuario_uid = ? AND m.rol = 'PRODUCT_OWNER'
            LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            ps.setLong(3, proyectoId);
            ps.setString(4, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isMiembroOfProyecto(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT 1 FROM miembros_proyecto
             WHERE proyecto_id = ? AND usuario_uid = ?
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public MiembroRow insertMiembro(long proyectoId, String usuarioUid, String rol) {
        final String sql = """
            WITH ins AS (
                INSERT INTO miembros_proyecto (proyecto_id, usuario_uid, rol)
                VALUES (?, ?, ?)
                RETURNING id, proyecto_id, usuario_uid, rol
            )
            SELECT ins.id, ins.proyecto_id, ins.usuario_uid, ins.rol, u.email, u.nombre
              FROM ins
              JOIN usuarios u ON u.firebase_uid = ins.usuario_uid
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            ps.setString(3, rol);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return mapMiembroRow(rs);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int deleteMiembroById(long proyectoId, long miembroId) {
        final String sql = "DELETE FROM miembros_proyecto WHERE proyecto_id = ? AND id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setLong(2, miembroId);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void insertNotificacion(String usuarioUid, String texto) {
        insertNotificacion(usuarioUid, texto, null, null, null);
    }

    @Override
    public void insertNotificacion(String usuarioUid, String texto, String tipo, Long invitacionId, Long proyectoId) {
        final String sql = """
            INSERT INTO notificaciones (usuario_uid, texto, leida, tipo, invitacion_id, proyecto_id)
            VALUES (?, ?, FALSE, ?, ?, ?)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, texto);
            if (tipo == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, tipo);
            if (invitacionId == null) ps.setNull(4, Types.BIGINT);
            else ps.setLong(4, invitacionId);
            if (proyectoId == null) ps.setNull(5, Types.BIGINT);
            else ps.setLong(5, proyectoId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<Long> selectUniversidadIdByUsuarioUid(String usuarioUid) {
        final String sql = "SELECT universidad_id FROM usuarios WHERE firebase_uid = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getObject("universidad_id", Long.class));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<AsignaturaRow> selectAsignaturasByUniversidadId(long universidadId) {
        final String sql = """
            SELECT id, universidad_id, nombre, descripcion, tutor_nombre
              FROM asignaturas WHERE universidad_id = ? ORDER BY nombre
            """;
        List<AsignaturaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAsignaturaRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<AsignaturaRow> selectAsignaturasDisponiblesParaProfesor(long universidadId, String profesorUid) {
        final String sql = """
            SELECT a.id, a.universidad_id, a.nombre, a.descripcion, a.tutor_nombre
              FROM asignaturas a
             WHERE a.universidad_id = ?
               AND NOT EXISTS (
                   SELECT 1 FROM profesor_asignaturas pa
                    WHERE pa.asignatura_id = a.id
                      AND pa.usuario_uid <> ?
               )
             ORDER BY a.nombre
            """;
        List<AsignaturaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, universidadId);
            ps.setString(2, profesorUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAsignaturaRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    private AsignaturaRow mapAsignaturaRow(ResultSet rs) throws SQLException {
        return AsignaturaRow.builder()
                .id(rs.getLong("id"))
                .universidadId(rs.getLong("universidad_id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .tutorNombre(rs.getString("tutor_nombre"))
                .build();
    }

    @Override
    public Optional<Long> selectUniversidadIdByAsignaturaId(long asignaturaId) {
        final String sql = "SELECT universidad_id FROM asignaturas WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, asignaturaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("universidad_id"));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Long> selectAsignaturaIdByPlantillaId(long plantillaId) {
        final String sql = "SELECT asignatura_id FROM plantillas_proyecto WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("asignatura_id"));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<PlantillaProyectoRow> selectPlantillasByAsignaturaId(long asignaturaId) {
        final String sql = """
            SELECT pp.id, pp.asignatura_id, a.nombre AS asignatura_nombre, pp.titulo, pp.descripcion, pp.orden,
                   pp.fecha_inicio_sugerida, pp.fecha_fin_sugerida, a.tutor_nombre,
                   (SELECT COUNT(*)::int FROM plantillas_tarea pt WHERE pt.plantilla_proyecto_id = pp.id) AS num_tareas,
                   (SELECT COUNT(*)::int FROM plantillas_hito ph WHERE ph.plantilla_proyecto_id = pp.id) AS num_hitos
              FROM plantillas_proyecto pp
              JOIN asignaturas a ON a.id = pp.asignatura_id
             WHERE pp.asignatura_id = ?
             ORDER BY pp.orden, pp.id
            """;
        return queryPlantillas(sql, ps -> ps.setLong(1, asignaturaId));
    }

    @Override
    public Optional<PlantillaProyectoRow> selectPlantillaById(long plantillaId) {
        final String sql = """
            SELECT pp.id, pp.asignatura_id, a.nombre AS asignatura_nombre, pp.titulo, pp.descripcion, pp.orden,
                   pp.fecha_inicio_sugerida, pp.fecha_fin_sugerida, a.tutor_nombre,
                   (SELECT COUNT(*)::int FROM plantillas_tarea pt WHERE pt.plantilla_proyecto_id = pp.id) AS num_tareas,
                   (SELECT COUNT(*)::int FROM plantillas_hito ph WHERE ph.plantilla_proyecto_id = pp.id) AS num_hitos
              FROM plantillas_proyecto pp
              JOIN asignaturas a ON a.id = pp.asignatura_id
             WHERE pp.id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPlantillaProyectoRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<PlantillaTareaRow> selectTareasByPlantillaId(long plantillaId) {
        final String sql = """
            SELECT id, titulo, descripcion, orden, fecha_limite_sugerida
              FROM plantillas_tarea WHERE plantilla_proyecto_id = ? ORDER BY orden, id
            """;
        List<PlantillaTareaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(PlantillaTareaRow.builder()
                            .id(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .descripcion(rs.getString("descripcion"))
                            .orden(rs.getInt("orden"))
                            .fechaLimiteSugerida(toLocalDate(rs.getDate("fecha_limite_sugerida")))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<PlantillaHitoRow> selectHitosByPlantillaId(long plantillaId) {
        final String sql = """
            SELECT id, titulo, fecha_sugerida, orden
              FROM plantillas_hito WHERE plantilla_proyecto_id = ? ORDER BY orden, id
            """;
        List<PlantillaHitoRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(PlantillaHitoRow.builder()
                            .id(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .fechaSugerida(rs.getDate("fecha_sugerida").toLocalDate())
                            .orden(rs.getInt("orden"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    private List<PlantillaProyectoRow> queryPlantillas(String sql, StatementSetter setter) {
        List<PlantillaProyectoRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPlantillaProyectoRow(rs));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    private PlantillaProyectoRow mapPlantillaProyectoRow(ResultSet rs) throws SQLException {
        return PlantillaProyectoRow.builder()
                .id(rs.getLong("id"))
                .asignaturaId(rs.getLong("asignatura_id"))
                .asignaturaNombre(rs.getString("asignatura_nombre"))
                .titulo(rs.getString("titulo"))
                .descripcion(rs.getString("descripcion"))
                .orden(rs.getInt("orden"))
                .fechaInicioSugerida(toLocalDate(rs.getDate("fecha_inicio_sugerida")))
                .fechaFinSugerida(toLocalDate(rs.getDate("fecha_fin_sugerida")))
                .tutorNombre(rs.getString("tutor_nombre"))
                .numTareas(rs.getInt("num_tareas"))
                .numHitos(rs.getInt("num_hitos"))
                .build();
    }

    private MiembroRow mapMiembroRow(ResultSet rs) throws SQLException {
        return MiembroRow.builder()
                .id(rs.getLong("id"))
                .proyectoId(rs.getLong("proyecto_id"))
                .usuarioUid(rs.getString("usuario_uid"))
                .rol(rs.getString("rol"))
                .email(rs.getString("email"))
                .nombre(rs.getString("nombre"))
                .build();
    }

    private HitoRow mapHitoRow(ResultSet rs) throws SQLException {
        return HitoRow.builder()
                .id(rs.getLong("id"))
                .proyectoId(rs.getLong("proyecto_id"))
                .titulo(rs.getString("titulo"))
                .fecha(rs.getDate("fecha").toLocalDate())
                .completado(rs.getBoolean("completado"))
                .build();
    }

    @FunctionalInterface
    private interface StatementSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<ProyectoRow> queryProyectos(String sql, StatementSetter setter) {
        List<ProyectoRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProyectoRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    private ProyectoRow mapProyectoRow(ResultSet rs) throws SQLException {
        return ProyectoRow.builder()
                .id(rs.getLong("id"))
                .titulo(rs.getString("titulo"))
                .descripcion(rs.getString("descripcion"))
                .fechaInicio(toLocalDate(rs.getDate("fecha_inicio")))
                .fechaFin(toLocalDate(rs.getDate("fecha_fin")))
                .estado(rs.getString("estado"))
                .propietarioUid(rs.getString("propietario_uid"))
                .plantillaId(rs.getObject("plantilla_id", Long.class))
                .asignaturaId(rs.getObject("asignatura_id", Long.class))
                .asignaturaNombre(rs.getString("asignatura_nombre"))
                .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                .actualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime())
                .build();
    }

    private static LocalDate toLocalDate(Date d) { return d == null ? null : d.toLocalDate(); }

    private static void setDate(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date == null) ps.setNull(index, Types.DATE);
        else ps.setDate(index, Date.valueOf(date));
    }

    @Override
    public boolean existsProyectoByPropietarioAndPlantilla(String propietarioUid, long plantillaId) {
        final String sql = "SELECT 1 FROM proyectos WHERE propietario_uid = ? AND plantilla_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, propietarioUid);
            ps.setLong(2, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean existsMiembroEnProyectoDePlantilla(String usuarioUid, long plantillaId) {
        final String sql = """
            SELECT 1
              FROM miembros_proyecto m
              JOIN proyectos p ON p.id = m.proyecto_id
             WHERE m.usuario_uid = ? AND p.plantilla_id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean existsMiembroEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId) {
        final String sql = """
            SELECT 1
              FROM miembros_proyecto m
              JOIN proyectos p ON p.id = m.proyecto_id
             WHERE m.usuario_uid = ? AND p.plantilla_id = ? AND p.id <> ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, plantillaId);
            ps.setLong(3, excludeProyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean existsPropietarioEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId) {
        final String sql = """
            SELECT 1 FROM proyectos
             WHERE propietario_uid = ? AND plantilla_id = ? AND id <> ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, plantillaId);
            ps.setLong(3, excludeProyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<Long> selectPlantillaIdByProyectoId(long proyectoId) {
        final String sql = "SELECT plantilla_id FROM proyectos WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getObject("plantilla_id", Long.class));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<String> selectTutorDemoUidByPlantillaId(long plantillaId) {
        final String sql = """
            SELECT a.tutor_demo_uid
              FROM plantillas_proyecto pp
              JOIN asignaturas a ON a.id = pp.asignatura_id
             WHERE pp.id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("tutor_demo_uid"));
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void insertTareaInstancia(long proyectoId, String titulo, String descripcion, int orden, LocalDate fechaLimite) {
        final String sql = """
            INSERT INTO tareas (proyecto_id, titulo, descripcion, estado, prioridad, fecha_limite, orden, origen)
            VALUES (?, ?, ?, 'PENDIENTE', 'MEDIA', ?, ?, 'PROFESOR')
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            setDate(ps, 4, fechaLimite);
            ps.setInt(5, orden);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isProfesorDeAsignatura(String usuarioUid, long asignaturaId) {
        final String sql = """
            SELECT 1 FROM usuarios u
              JOIN profesor_asignaturas pa ON pa.usuario_uid = u.firebase_uid
             WHERE u.firebase_uid = ? AND u.tipo = 'PROFESOR' AND pa.asignatura_id = ?
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, asignaturaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isProfesorSupervisorOfProyecto(String usuarioUid, long proyectoId) {
        final String sql = """
            SELECT 1 FROM proyectos p
              JOIN profesor_asignaturas pa ON pa.asignatura_id = p.asignatura_id
             WHERE p.id = ? AND pa.usuario_uid = ? AND p.asignatura_id IS NOT NULL
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<ProyectoSupervisionRow> selectProyectosByAsignaturaId(long asignaturaId) {
        final String sql = """
            SELECT p.id, p.titulo, p.estado, p.fecha_inicio, p.fecha_fin, p.actualizado_en,
                   u.nombre AS propietario_nombre, u.email AS propietario_email
              FROM proyectos p
              JOIN usuarios u ON u.firebase_uid = p.propietario_uid
             WHERE p.asignatura_id = ?
             ORDER BY p.actualizado_en DESC
            """;
        List<ProyectoSupervisionRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, asignaturaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(ProyectoSupervisionRow.builder()
                            .id(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .estado(rs.getString("estado"))
                            .propietarioNombre(rs.getString("propietario_nombre"))
                            .propietarioEmail(rs.getString("propietario_email"))
                            .fechaInicio(getLocalDate(rs, "fecha_inicio"))
                            .fechaFin(getLocalDate(rs, "fecha_fin"))
                            .actualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime())
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public List<GrupoSupervisionMemberRow> selectGruposByPlantillaId(long plantillaId) {
        final String sql = """
            SELECT p.id AS proyecto_id, p.titulo AS proyecto_titulo, p.estado AS proyecto_estado,
                   p.fecha_inicio, p.fecha_fin, p.actualizado_en, p.propietario_uid,
                   m.usuario_uid AS miembro_uid, u.nombre AS miembro_nombre, u.email AS miembro_email,
                   m.rol AS miembro_rol
              FROM proyectos p
              LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
              LEFT JOIN usuarios u ON u.firebase_uid = m.usuario_uid
             WHERE p.plantilla_id = ?
             ORDER BY p.actualizado_en DESC, p.id, m.id
            """;
        List<GrupoSupervisionMemberRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(GrupoSupervisionMemberRow.builder()
                            .proyectoId(rs.getLong("proyecto_id"))
                            .proyectoTitulo(rs.getString("proyecto_titulo"))
                            .proyectoEstado(rs.getString("proyecto_estado"))
                            .fechaInicio(getLocalDate(rs, "fecha_inicio"))
                            .fechaFin(getLocalDate(rs, "fecha_fin"))
                            .actualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime())
                            .propietarioUid(rs.getString("propietario_uid"))
                            .miembroUid(rs.getString("miembro_uid"))
                            .miembroNombre(rs.getString("miembro_nombre"))
                            .miembroEmail(rs.getString("miembro_email"))
                            .miembroRol(rs.getString("miembro_rol"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public int selectMaxOrdenPlantillaByAsignatura(long asignaturaId) {
        final String sql = "SELECT COALESCE(MAX(orden), 0) FROM plantillas_proyecto WHERE asignatura_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, asignaturaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return 0;
    }

    @Override
    public long insertPlantillaProyecto(long asignaturaId, String titulo, String descripcion, int orden,
                                        LocalDate fechaInicio, LocalDate fechaFin) {
        final String sql = """
            INSERT INTO plantillas_proyecto (asignatura_id, titulo, descripcion, orden, fecha_inicio_sugerida, fecha_fin_sugerida)
            VALUES (?, ?, ?, ?, ?, ?) RETURNING id
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, asignaturaId);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            ps.setInt(4, orden);
            setDate(ps, 5, fechaInicio);
            setDate(ps, 6, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("id");
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void insertPlantillaTarea(long plantillaId, String titulo, String descripcion, int orden, LocalDate fechaLimite) {
        final String sql = """
            INSERT INTO plantillas_tarea (plantilla_proyecto_id, titulo, descripcion, orden, fecha_limite_sugerida)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            ps.setInt(4, orden);
            setDate(ps, 5, fechaLimite);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void insertPlantillaHito(long plantillaId, String titulo, LocalDate fecha, int orden) {
        final String sql = """
            INSERT INTO plantillas_hito (plantilla_proyecto_id, titulo, fecha_sugerida, orden)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, plantillaId);
            ps.setString(2, titulo);
            setDate(ps, 3, fecha);
            ps.setInt(4, orden);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    private LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, LocalDate.class);
    }

    @Override
    public void updatePlantillaProyecto(long plantillaId, String titulo, String descripcion, int orden,
                                        LocalDate fechaInicio, LocalDate fechaFin) {
        final String sql = """
            UPDATE plantillas_proyecto
               SET titulo = ?, descripcion = ?, orden = ?, fecha_inicio_sugerida = ?, fecha_fin_sugerida = ?
             WHERE id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, descripcion);
            ps.setInt(3, orden);
            setDate(ps, 4, fechaInicio);
            setDate(ps, 5, fechaFin);
            ps.setLong(6, plantillaId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void deletePlantillaTareasByPlantilla(long plantillaId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM plantillas_tarea WHERE plantilla_proyecto_id = ?")) {
            ps.setLong(1, plantillaId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void deletePlantillaHitosByPlantilla(long plantillaId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM plantillas_hito WHERE plantilla_proyecto_id = ?")) {
            ps.setLong(1, plantillaId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isProfesor(String usuarioUid) {
        final String sql = "SELECT tipo FROM usuarios WHERE firebase_uid = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && "PROFESOR".equals(rs.getString("tipo"));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<AsignaturaRow> selectAsignaturasMatriculadasByUsuario(String usuarioUid, long universidadId) {
        final String sql = """
            SELECT a.id, a.universidad_id, a.nombre, a.descripcion, a.tutor_nombre
              FROM usuario_asignaturas ua
              JOIN asignaturas a ON a.id = ua.asignatura_id
             WHERE ua.usuario_uid = ? AND a.universidad_id = ?
             ORDER BY a.nombre
            """;
        List<AsignaturaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, universidadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(AsignaturaRow.builder()
                            .id(rs.getLong("id"))
                            .universidadId(rs.getLong("universidad_id"))
                            .nombre(rs.getString("nombre"))
                            .descripcion(rs.getString("descripcion"))
                            .tutorNombre(rs.getString("tutor_nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public InvitacionProyectoRow insertInvitacionProyecto(long proyectoId, String usuarioUid, String rol, String invitadoPorUid) {
        final String sql = """
            WITH ins AS (
                INSERT INTO invitaciones_proyecto (proyecto_id, usuario_invitado_uid, rol, estado, invitado_por_uid)
                VALUES (?, ?, ?, 'PENDIENTE', ?)
                RETURNING id, proyecto_id, usuario_invitado_uid, rol, estado, invitado_por_uid, creado_en
            )
            SELECT ins.id, ins.proyecto_id, ins.usuario_invitado_uid, ins.rol, ins.estado,
                   ins.invitado_por_uid, ins.creado_en, u.email, u.nombre, inv.nombre AS invitado_por_nombre
              FROM ins
              JOIN usuarios u ON u.firebase_uid = ins.usuario_invitado_uid
              JOIN usuarios inv ON inv.firebase_uid = ins.invitado_por_uid
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            ps.setString(3, rol);
            ps.setString(4, invitadoPorUid);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return mapInvitacionProyectoRow(rs);
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<InvitacionProyectoRow> selectInvitacionesPendientesByProyecto(long proyectoId) {
        final String sql = """
            SELECT i.id, i.proyecto_id, i.usuario_invitado_uid, i.rol, i.estado,
                   i.invitado_por_uid, i.creado_en, u.email, u.nombre, inv.nombre AS invitado_por_nombre
              FROM invitaciones_proyecto i
              JOIN usuarios u ON u.firebase_uid = i.usuario_invitado_uid
              JOIN usuarios inv ON inv.firebase_uid = i.invitado_por_uid
             WHERE i.proyecto_id = ? AND i.estado = 'PENDIENTE'
             ORDER BY i.creado_en
            """;
        List<InvitacionProyectoRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapInvitacionProyectoRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public Optional<InvitacionProyectoRow> selectInvitacionById(long invitacionId) {
        final String sql = """
            SELECT i.id, i.proyecto_id, i.usuario_invitado_uid, i.rol, i.estado,
                   i.invitado_por_uid, i.creado_en, u.email, u.nombre, inv.nombre AS invitado_por_nombre
              FROM invitaciones_proyecto i
              JOIN usuarios u ON u.firebase_uid = i.usuario_invitado_uid
              JOIN usuarios inv ON inv.firebase_uid = i.invitado_por_uid
             WHERE i.id = ?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, invitacionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapInvitacionProyectoRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public boolean updateInvitacionEstado(long invitacionId, String estado) {
        final String sql = "UPDATE invitaciones_proyecto SET estado = ? WHERE id = ? AND estado = 'PENDIENTE'";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setLong(2, invitacionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean existsInvitacionPendiente(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT 1 FROM invitaciones_proyecto
             WHERE proyecto_id = ? AND usuario_invitado_uid = ? AND estado = 'PENDIENTE'
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isUsuarioMatriculadoEnAsignatura(String usuarioUid, long asignaturaId) {
        final String sql = "SELECT 1 FROM usuario_asignaturas WHERE usuario_uid = ? AND asignatura_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setLong(2, asignaturaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<Long> selectAsignaturaIdByProyectoId(long proyectoId) {
        final String sql = "SELECT asignatura_id FROM proyectos WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(rs.getObject("asignatura_id", Long.class));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    private InvitacionProyectoRow mapInvitacionProyectoRow(ResultSet rs) throws SQLException {
        return InvitacionProyectoRow.builder()
                .id(rs.getLong("id"))
                .proyectoId(rs.getLong("proyecto_id"))
                .usuarioUid(rs.getString("usuario_invitado_uid"))
                .email(rs.getString("email"))
                .nombre(rs.getString("nombre"))
                .rol(rs.getString("rol"))
                .estado(rs.getString("estado"))
                .invitadoPorUid(rs.getString("invitado_por_uid"))
                .invitadoPorNombre(rs.getString("invitado_por_nombre"))
                .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                .build();
    }
}
