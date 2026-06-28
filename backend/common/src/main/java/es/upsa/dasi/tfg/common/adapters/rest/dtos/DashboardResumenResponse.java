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
@Schema(description = "Datos agregados del panel principal: proyectos e informes del usuario")
public class DashboardResumenResponse
{
    @Schema(description = "Proyectos en los que participa el usuario autenticado")
    List<ProyectoResponse> proyectos;

    @Schema(description = "Resumen estadístico de tareas, hitos y actividad")
    InformesResumenResponse informes;
}
