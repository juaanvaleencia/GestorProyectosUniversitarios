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
        if (!hasMemberAccess(proyectoId, currentUid())) {
            throw new ForbiddenTfgException("No tienes acceso a las tareas de este proyecto");
        }
    }

    public void requireViewAccess(long proyectoId)
    {
        String uid = currentUid();
        if (hasMemberAccess(proyectoId, uid) || isProfesorSupervisor(proyectoId, uid)) {
            return;
        }
        throw new ForbiddenTfgException("No tienes acceso a las tareas de este proyecto");
    }

    private boolean hasMemberAccess(long proyectoId, String uid)
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
            ps.setString(2, uid);
            ps.setString(3, uid);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new TfgRuntimeException(e);
        }
    }

    public void requireProductOwner(long proyectoId)
    {
        if (!isProductOwner(proyectoId, currentUid())) {
            throw new ForbiddenTfgException("Solo el Product Owner puede modificar este proyecto");
        }
    }

    private boolean isProductOwner(long proyectoId, String uid)
    {
        final String sql = """
            SELECT 1 FROM proyectos p
             WHERE p.id = ? AND p.propietario_uid = ?
            UNION ALL
            SELECT 1 FROM miembros_proyecto m
             WHERE m.proyecto_id = ? AND m.usuario_uid = ? AND m.rol = 'PRODUCT_OWNER'
            LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, proyectoId);
            ps.setString(2, uid);
            ps.setLong(3, proyectoId);
            ps.setString(4, uid);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new TfgRuntimeException(e);
        }
    }

    private boolean isProfesorSupervisor(long proyectoId, String uid)
    {
        final String sql = """
            SELECT 1 FROM proyectos p
              JOIN profesor_asignaturas pa ON pa.asignatura_id = p.asignatura_id
             WHERE p.id = ? AND pa.usuario_uid = ? AND p.asignatura_id IS NOT NULL
             LIMIT 1
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setLong(1, proyectoId);
            ps.setString(2, uid);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new TfgRuntimeException(e);
        }
    }
}
