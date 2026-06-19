package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ProyectoRow;
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
                   p.propietario_uid, p.creado_en, p.actualizado_en
              FROM proyectos p
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
            SELECT id, titulo, descripcion, fecha_inicio, fecha_fin, estado, propietario_uid, creado_en, actualizado_en
              FROM proyectos WHERE id = ?
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
            INSERT INTO proyectos (titulo, descripcion, fecha_inicio, fecha_fin, estado, propietario_uid)
            VALUES (?, ?, ?, ?, ?, ?) RETURNING id, creado_en, actualizado_en
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getTitulo());
            ps.setString(2, row.getDescripcion());
            setDate(ps, 3, row.getFechaInicio());
            setDate(ps, 4, row.getFechaFin());
            ps.setString(5, row.getEstado());
            ps.setString(6, row.getPropietarioUid());
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
                    list.add(HitoRow.builder()
                            .id(rs.getLong("id"))
                            .proyectoId(rs.getLong("proyecto_id"))
                            .titulo(rs.getString("titulo"))
                            .fecha(rs.getDate("fecha").toLocalDate())
                            .completado(rs.getBoolean("completado"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
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
    public List<MiembroRow> selectMiembrosByProyecto(long proyectoId) {
        final String sql = """
            SELECT m.id, m.proyecto_id, m.usuario_uid, m.rol, u.email, u.nombre
              FROM miembros_proyecto m JOIN usuarios u ON u.firebase_uid = m.usuario_uid
             WHERE m.proyecto_id = ?
            """;
        List<MiembroRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(MiembroRow.builder()
                            .id(rs.getLong("id"))
                            .proyectoId(rs.getLong("proyecto_id"))
                            .usuarioUid(rs.getString("usuario_uid"))
                            .rol(rs.getString("rol"))
                            .email(rs.getString("email"))
                            .nombre(rs.getString("nombre"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
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
    public MiembroRow insertMiembro(long proyectoId, String usuarioUid, String rol) {
        final String sql = """
            INSERT INTO miembros_proyecto (proyecto_id, usuario_uid, rol)
            VALUES (?, ?, ?)
            RETURNING id, proyecto_id, usuario_uid, rol
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            ps.setString(2, usuarioUid);
            ps.setString(3, rol);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return MiembroRow.builder()
                        .id(rs.getLong("id"))
                        .proyectoId(rs.getLong("proyecto_id"))
                        .usuarioUid(rs.getString("usuario_uid"))
                        .rol(rs.getString("rol"))
                        .build();
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
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
                .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                .actualizadoEn(rs.getTimestamp("actualizado_en").toLocalDateTime())
                .build();
    }

    private static LocalDate toLocalDate(Date d) { return d == null ? null : d.toLocalDate(); }

    private static void setDate(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date == null) ps.setNull(index, Types.DATE);
        else ps.setDate(index, Date.valueOf(date));
    }
}
