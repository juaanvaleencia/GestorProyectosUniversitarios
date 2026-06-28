package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.DashboardResumenResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Dashboard", description = "Datos agregados para el panel principal")
@SecurityRequirements(value = @SecurityRequirement(name = "firebase-jwt"))
@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource
{
    Repository repository;

    @Inject
    public DashboardResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public DashboardResumenResponse resumen() throws TfgException {
        return repository.findDashboardResumen();
    }
}
