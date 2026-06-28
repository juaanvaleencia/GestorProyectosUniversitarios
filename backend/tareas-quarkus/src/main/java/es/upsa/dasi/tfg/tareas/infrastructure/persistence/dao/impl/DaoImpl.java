package es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.dtos.TareaRow;
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
    private static final String TAREA_COLUMNS = """
            id, proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid,
            fecha_limite, orden, creado_en, origen, tarea_padre_id, letra_subtarea
            """;

    private final DataSource dataSource;

    @Inject
    public DaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<TareaRow> selectTareasByProyecto(long proyectoId) {
        final String sql = """
            SELECT %s FROM tareas WHERE proyecto_id = ? ORDER BY orden, id, letra_subtarea
            """.formatted(TAREA_COLUMNS);
        List<TareaRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTareaRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }

    @Override
    public Optional<TareaRow> selectTareaById(long id) {
        final String sql = "SELECT %s FROM tareas WHERE id = ?".formatted(TAREA_COLUMNS);
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapTareaRow(rs));
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public TareaRow insertTarea(TareaRow row)
    {
        final String sql = """
            INSERT INTO tareas (proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid,
                                fecha_limite, orden, origen, tarea_padre_id, letra_subtarea)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, creado_en
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, row.getProyectoId());
            ps.setString(2, row.getTitulo());
            ps.setString(3, row.getDescripcion());
            ps.setString(4, row.getEstado());
            ps.setString(5, row.getPrioridad());
            ps.setString(6, row.getResponsableUid());
            if (row.getFechaLimite() == null) ps.setNull(7, Types.DATE);
            else ps.setDate(7, Date.valueOf(row.getFechaLimite()));
            ps.setInt(8, row.getOrden());
            ps.setString(9, row.getOrigen() != null ? row.getOrigen() : "ALUMNO");
            if (row.getTareaPadreId() == null) ps.setNull(10, Types.BIGINT);
            else ps.setLong(10, row.getTareaPadreId());
            ps.setString(11, row.getLetraSubtarea());
            try (ResultSet rs = ps.executeQuery())
            {
                rs.next();
                return row.withId(rs.getLong("id"))
                        .withCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
            }
        }
        catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public Optional<TareaRow> updateTarea(TareaRow row)
    {
        final String sql = """
            UPDATE tareas SET titulo=?, descripcion=?, estado=?, prioridad=?, responsable_uid=?,
                              fecha_limite=?, orden=?
             WHERE id=? AND proyecto_id=?
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, row.getTitulo());
            ps.setString(2, row.getDescripcion());
            ps.setString(3, row.getEstado());
            ps.setString(4, row.getPrioridad());
            ps.setString(5, row.getResponsableUid());
            if (row.getFechaLimite() == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, Date.valueOf(row.getFechaLimite()));
            ps.setInt(7, row.getOrden());
            ps.setLong(8, row.getId());
            ps.setLong(9, row.getProyectoId());
            return ps.executeUpdate() == 0 ? Optional.empty() : Optional.of(row);
        }
        catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int deleteTareaById(long id)
    {
        final String sql = "DELETE FROM tareas WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
        catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public void insertNotificacion(String usuarioUid, String texto) {
        final String sql = "INSERT INTO notificaciones (usuario_uid, texto, leida) VALUES (?, ?, FALSE)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, texto);
            ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public boolean isUsuarioTutorEnProyecto(long proyectoId, String usuarioUid) {
        final String sql = """
            SELECT 1 FROM miembros_proyecto
             WHERE proyecto_id = ? AND usuario_uid = ? AND rol = 'TUTOR'
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
    public int countSubtareasByPadre(long tareaPadreId) {
        final String sql = "SELECT COUNT(*) FROM tareas WHERE tarea_padre_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, tareaPadreId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int countSubtareasSinResponsableByPadre(long tareaPadreId) {
        final String sql = """
            SELECT COUNT(*) FROM tareas
             WHERE tarea_padre_id = ?
               AND (responsable_uid IS NULL OR TRIM(responsable_uid) = '')
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, tareaPadreId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public int updateEstadoSubtareasByPadreEnColumna(long tareaPadreId, String estadoOrigen, String estadoDestino) {
        final String sql = """
            UPDATE tareas SET estado = ?
             WHERE tarea_padre_id = ?
               AND UPPER(REPLACE(estado, '-', '_')) = UPPER(REPLACE(?, '-', '_'))
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estadoDestino);
            ps.setLong(2, tareaPadreId);
            ps.setString(3, estadoOrigen);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    private TareaRow mapTareaRow(ResultSet rs) throws SQLException {
        return TareaRow.builder()
                .id(rs.getLong("id"))
                .proyectoId(rs.getLong("proyecto_id"))
                .titulo(rs.getString("titulo"))
                .descripcion(rs.getString("descripcion"))
                .estado(rs.getString("estado"))
                .prioridad(rs.getString("prioridad"))
                .responsableUid(rs.getString("responsable_uid"))
                .fechaLimite(toLocalDate(rs.getDate("fecha_limite")))
                .orden(rs.getInt("orden"))
                .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                .origen(rs.getString("origen"))
                .tareaPadreId(rs.getObject("tarea_padre_id", Long.class))
                .letraSubtarea(rs.getString("letra_subtarea"))
                .build();
    }

    private static LocalDate toLocalDate(Date d) { return d == null ? null : d.toLocalDate(); }
}
