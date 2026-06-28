package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.ActividadDiaRow;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeHitoPendienteRow;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeResumenRow;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeTareaPendienteRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class DaoImpl implements Dao
{
    private static final DateTimeFormatter DIA_CORTO = DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-ES"));

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
        int hitosCompletados = countHitos(usuarioUid, true);
        int hitosPendientes = countHitos(usuarioUid, false);
        int totalTareas = tareasCompletadas + tareasPendientes;
        int progresoMedio = totalTareas == 0 ? 0 : (tareasCompletadas * 100) / totalTareas;
        List<ActividadDiaRow> actividad = actividadSemanal(usuarioUid);
        List<InformeTareaPendienteRow> tareasDetalle = listTareasPendientes(usuarioUid);
        List<InformeHitoPendienteRow> hitosDetalle = listHitosPendientes(usuarioUid);

        String mensaje = proyectosActivos == 0 && totalTareas == 0
                ? "Sin proyectos ni tareas registradas"
                : "Resumen de tus proyectos";

        return InformeResumenRow.builder()
                .mensaje(mensaje)
                .proyectosActivos(proyectosActivos)
                .tareasCompletadas(tareasCompletadas)
                .tareasPendientes(tareasPendientes)
                .hitosCompletados(hitosCompletados)
                .hitosPendientes(hitosPendientes)
                .progresoMedio(progresoMedio)
                .actividadSemanal(actividad)
                .tareasPendientesDetalle(tareasDetalle)
                .hitosPendientesDetalle(hitosDetalle)
                .build();
    }

    private int countHitos(String usuarioUid, boolean completado) {
        final String sql = """
            SELECT COUNT(*)
              FROM hitos h
             WHERE h.proyecto_id IN (%s)
               AND h.completado = ?
            """.formatted(FILTRO_MIS_PROYECTOS);
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            ps.setBoolean(3, completado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
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
               AND t.tarea_padre_id IS NULL
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
               AND t.tarea_padre_id IS NULL
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
        LocalDate hoy = LocalDate.now();
        Map<LocalDate, Integer> porFecha = contarTareasPorFecha(usuarioUid, hoy, hoy.plusDays(6));

        List<ActividadDiaRow> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate fecha = hoy.plusDays(i);
            list.add(ActividadDiaRow.builder()
                    .dia(etiquetaDia(fecha))
                    .fecha(fecha.toString())
                    .tareas(porFecha.getOrDefault(fecha, 0))
                    .build());
        }
        return list;
    }

    private Map<LocalDate, Integer> contarTareasPorFecha(String usuarioUid, LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Integer> porFecha = new HashMap<>();

        final String sql = """
            SELECT t.fecha_limite::date AS fecha, COUNT(*)::int AS total
              FROM tareas t
             WHERE t.proyecto_id IN (%s)
               AND t.fecha_limite IS NOT NULL
               AND t.tarea_padre_id IS NULL
               AND t.fecha_limite >= ?
               AND t.fecha_limite <= ?
               AND t.estado <> 'HECHA'
             GROUP BY t.fecha_limite::date
            """.formatted(FILTRO_MIS_PROYECTOS);

        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            ps.setDate(3, Date.valueOf(desde));
            ps.setDate(4, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    porFecha.put(fecha, rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }

        return porFecha;
    }

    private static String etiquetaDia(LocalDate fecha) {
        String raw = fecha.format(DIA_CORTO).replace(".", "");
        if (raw.isEmpty()) {
            return raw;
        }
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    private List<InformeTareaPendienteRow> listTareasPendientes(String usuarioUid) {
        final String sql = """
            SELECT t.id, t.titulo, t.estado, t.fecha_limite::text AS fecha_limite,
                   p.id AS proyecto_id, p.titulo AS proyecto_titulo
              FROM tareas t
              JOIN proyectos p ON p.id = t.proyecto_id
             WHERE t.proyecto_id IN (%s)
               AND t.estado <> 'HECHA'
               AND t.tarea_padre_id IS NULL
             ORDER BY t.fecha_limite NULLS LAST, p.titulo, t.titulo
            """.formatted(FILTRO_MIS_PROYECTOS);
        List<InformeTareaPendienteRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(InformeTareaPendienteRow.builder()
                            .id(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .estado(rs.getString("estado"))
                            .fechaLimite(rs.getString("fecha_limite"))
                            .proyectoId(rs.getLong("proyecto_id"))
                            .proyectoTitulo(rs.getString("proyecto_titulo"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
        return list;
    }

    private List<InformeHitoPendienteRow> listHitosPendientes(String usuarioUid) {
        final String sql = """
            SELECT h.id, h.titulo, h.fecha::text AS fecha,
                   p.id AS proyecto_id, p.titulo AS proyecto_titulo
              FROM hitos h
              JOIN proyectos p ON p.id = h.proyecto_id
             WHERE h.proyecto_id IN (%s)
               AND h.completado = FALSE
             ORDER BY h.fecha, p.titulo, h.titulo
            """.formatted(FILTRO_MIS_PROYECTOS);
        List<InformeHitoPendienteRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            ps.setString(2, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(InformeHitoPendienteRow.builder()
                            .id(rs.getLong("id"))
                            .titulo(rs.getString("titulo"))
                            .fecha(rs.getString("fecha"))
                            .proyectoId(rs.getLong("proyecto_id"))
                            .proyectoTitulo(rs.getString("proyecto_titulo"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new TfgRuntimeException(e);
        }
        return list;
    }
}
