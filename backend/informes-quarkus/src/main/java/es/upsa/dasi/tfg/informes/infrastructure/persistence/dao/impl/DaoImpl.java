package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.ActividadDiaRow;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeResumenRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DaoImpl implements Dao
{
    private static final String[] DIAS_SEMANA = {"Lun", "Mar", "Mie", "Jue", "Vie"};

    private static final String FILTRO_MIS_PROYECTOS = """
            SELECT DISTINCT p.id
              FROM proyectos p
              LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
             WHERE p.propietario_uid = ? OR m.usuario_uid = ?
            """;

    private final DataSource dataSource;

    @Inject
    public DaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public InformeResumenRow selectResumenByUsuario(String usuarioUid) {
        int proyectosActivos = countProyectosActivos(usuarioUid);
        int tareasCompletadas = countTareas(usuarioUid, "HECHA");
        int tareasPendientes = countTareasPendientes(usuarioUid);
        int totalTareas = tareasCompletadas + tareasPendientes;
        int progresoMedio = totalTareas == 0 ? 0 : (tareasCompletadas * 100) / totalTareas;
        List<ActividadDiaRow> actividad = actividadSemanal(usuarioUid);

        String mensaje = proyectosActivos == 0 && totalTareas == 0
                ? "Sin proyectos ni tareas registradas"
                : "Resumen de tus proyectos";

        return InformeResumenRow.builder()
                .mensaje(mensaje)
                .proyectosActivos(proyectosActivos)
                .tareasCompletadas(tareasCompletadas)
                .tareasPendientes(tareasPendientes)
                .progresoMedio(progresoMedio)
                .actividadSemanal(actividad)
                .build();
    }

    private int countProyectosActivos(String usuarioUid) {
        final String sql = """
            SELECT COUNT(DISTINCT p.id)
              FROM proyectos p
              LEFT JOIN miembros_proyecto m ON m.proyecto_id = p.id
             WHERE (p.propietario_uid = ? OR m.usuario_uid = ?)
               AND p.estado = 'EN_CURSO'
            """;
        return count(sql, usuarioUid);
    }

    private int countTareas(String usuarioUid, String estado) {
        final String sql = """
            SELECT COUNT(*)
              FROM tareas t
             WHERE t.proyecto_id IN (%s)
               AND t.estado = ?
            """.formatted(FILTRO_MIS_PROYECTOS);
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            ps.setString(3, estado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
    }

    private int countTareasPendientes(String usuarioUid) {
        final String sql = """
            SELECT COUNT(*)
              FROM tareas t
             WHERE t.proyecto_id IN (%s)
               AND t.estado <> 'HECHA'
            """.formatted(FILTRO_MIS_PROYECTOS);
        return count(sql, usuarioUid);
    }

    private int count(String sql, String usuarioUid) {
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
    }

    private List<ActividadDiaRow> actividadSemanal(String usuarioUid) {
        Map<String, Integer> porDia = new LinkedHashMap<>();
        for (String dia : DIAS_SEMANA) {
            porDia.put(dia, 0);
        }

        final String sql = """
            SELECT CASE EXTRACT(ISODOW FROM t.fecha_limite)
                     WHEN 1 THEN 'Lun'
                     WHEN 2 THEN 'Mar'
                     WHEN 3 THEN 'Mie'
                     WHEN 4 THEN 'Jue'
                     WHEN 5 THEN 'Vie'
                   END AS dia,
                   COUNT(*)::int AS total
              FROM tareas t
             WHERE t.proyecto_id IN (%s)
               AND t.fecha_limite IS NOT NULL
               AND t.fecha_limite >= date_trunc('week', CURRENT_DATE)::date
               AND t.fecha_limite < (date_trunc('week', CURRENT_DATE) + interval '7 days')::date
               AND EXTRACT(ISODOW FROM t.fecha_limite) BETWEEN 1 AND 5
             GROUP BY dia
            """.formatted(FILTRO_MIS_PROYECTOS);

        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dia = rs.getString("dia");
                    if (dia != null) {
                        porDia.put(dia, rs.getInt("total"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }

        List<ActividadDiaRow> list = new ArrayList<>();
        for (String dia : DIAS_SEMANA) {
            list.add(ActividadDiaRow.builder().dia(dia).tareas(porDia.get(dia)).build());
        }
        return list;
    }
}
