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
    private final DataSource dataSource;

    @Inject
    public DaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<TareaRow> selectTareasByProyecto(long proyectoId) {
        final String sql = """
            SELECT id, proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid, fecha_limite, orden, creado_en
              FROM tareas WHERE proyecto_id = ? ORDER BY orden, id
            """;
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
        final String sql = """
            SELECT id, proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid, fecha_limite, orden, creado_en
              FROM tareas WHERE id = ?
            """;
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
            INSERT INTO tareas (proyecto_id, titulo, descripcion, estado, prioridad, responsable_uid, fecha_limite, orden)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
            UPDATE tareas SET titulo=?, descripcion=?, estado=?, prioridad=?, responsable_uid=?, fecha_limite=?, orden=?
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
                .build();
    }

    private static LocalDate toLocalDate(Date d) { return d == null ? null : d.toLocalDate(); }
}
