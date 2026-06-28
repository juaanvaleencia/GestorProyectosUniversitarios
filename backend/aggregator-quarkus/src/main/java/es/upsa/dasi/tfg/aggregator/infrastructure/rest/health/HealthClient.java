package es.upsa.dasi.tfg.aggregator.infrastructure.rest.health;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.HealthResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "health-api")
@Path("/api/health")
public interface HealthClient
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    HealthResponse health();
}
