package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resumen estadístico de proyectos, tareas e hitos del usuario")
public class InformesResumenResponse
{
    String mensaje;
    int proyectosActivos;
    int tareasCompletadas;
    int tareasPendientes;
    int hitosCompletados;
    int hitosPendientes;
    int progresoMedio;
    List<ActividadDiaResponse> actividadSemanal;
    List<InformeTareaPendienteResponse> tareasPendientesDetalle;
    List<InformeHitoPendienteResponse> hitosPendientesDetalle;
}
