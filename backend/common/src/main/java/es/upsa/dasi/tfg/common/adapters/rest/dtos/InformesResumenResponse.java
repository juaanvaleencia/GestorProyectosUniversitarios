package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformesResumenResponse
{
    String mensaje;
    int proyectosActivos;
    int tareasCompletadas;
    int tareasPendientes;
    int progresoMedio;
    List<ActividadDiaResponse> actividadSemanal;
}
