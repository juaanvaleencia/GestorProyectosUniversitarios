package es.upsa.dasi.tfg.tareas.application;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class TareaAuthorizationService
{
    @Inject DataSource dataSource;
    @Inject JsonWebToken jwt;

    public String currentUid()
    {
        return jwt.getSubject();
    }

    public void requireMember(long proyectoId)
    {
        final String sql = """
            SELECT 1 FROM proyectos p
             LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
             WHERE p.id = ? AND (p.propietario_uid = ? OR m.usuario_uid = ?)
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, proyectoId);
            String uid = currentUid();
            ps.setString(2, uid);
            ps.setString(3, uid);
            try (ResultSet rs = ps.executeQuery())
            {
                if (!rs.next()) {
                    throw new ForbiddenTfgException("No tienes acceso a las tareas de este proyecto");
                }
            }
        }
        catch (SQLException e)
        {
            throw new TfgRuntimeException(e);
        }
    }

    public long proyectoIdOfTarea(long tareaId) throws ForbiddenTfgException
    {
        final String sql = "SELECT proyecto_id FROM tareas WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, tareaId);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getLong(1);
            }
        }
        catch (SQLException e)
        {
            throw new TfgRuntimeException(e);
        }
        throw new ForbiddenTfgException("Tarea no encontrada");
    }
}
