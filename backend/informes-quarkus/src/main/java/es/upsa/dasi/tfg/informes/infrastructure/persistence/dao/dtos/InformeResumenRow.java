package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InformeResumenRow
{
    String mensaje;
    int proyectosActivos;
    int tareasCompletadas;
    int tareasPendientes;
    int hitosCompletados;
    int hitosPendientes;
    int progresoMedio;
    List<ActividadDiaRow> actividadSemanal;
    List<InformeTareaPendienteRow> tareasPendientesDetalle;
    List<InformeHitoPendienteRow> hitosPendientesDetalle;
}
