package es.upsa.dasi.tfg.informes.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class InformeResumen
{
    String mensaje;
    int proyectosActivos;
    int tareasCompletadas;
    int tareasPendientes;
    int hitosCompletados;
    int hitosPendientes;
    int progresoMedio;
    List<ActividadDia> actividadSemanal;
    List<InformeTareaPendiente> tareasPendientesDetalle;
    List<InformeHitoPendiente> hitosPendientesDetalle;
}
