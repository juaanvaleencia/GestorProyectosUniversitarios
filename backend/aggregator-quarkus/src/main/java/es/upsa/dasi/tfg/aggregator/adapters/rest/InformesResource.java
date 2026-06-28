package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.InformesResumenResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Informes", description = "Resúmenes y estadísticas de actividad")
@Path("/api/informes")
@Produces(MediaType.APPLICATION_JSON)
public class InformesResource
{
    Repository repository;

    @Inject
    public InformesResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    @Path("resumen")
    public InformesResumenResponse resumen() throws TfgException {
        return repository.findInformesResumen();
    }
}
