package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estado de disponibilidad del servicio")
public class HealthResponse
{
    @Schema(description = "Estado del servicio", example = "UP")
    String status;

    @Schema(description = "Nombre del microservicio", example = "health-quarkus")
    String service;
}
