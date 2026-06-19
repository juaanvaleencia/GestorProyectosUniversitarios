package es.upsa.dasi.tfg.aggregator.infrastructure.rest.health;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@RegisterRestClient(configKey = "health-api")
@Path("/api/health")
public interface HealthClient
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, String> health();
}
