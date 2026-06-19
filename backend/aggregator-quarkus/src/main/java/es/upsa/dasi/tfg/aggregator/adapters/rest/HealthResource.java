package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/api/health")
public class HealthResource
{
    Repository repository;

    @Inject
    public HealthResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> health() {
        return repository.findHealth();
    }
}
