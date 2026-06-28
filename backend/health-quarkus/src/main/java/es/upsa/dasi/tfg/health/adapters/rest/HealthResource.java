package es.upsa.dasi.tfg.health.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.HealthResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Health", description = "Comprobación de estado del servicio")
@SecurityRequirements
@Path("/api/health")
public class HealthResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthResponse health()
    {
        return HealthResponse.builder()
                .status("UP")
                .service("health-quarkus")
                .build();
    }
}
