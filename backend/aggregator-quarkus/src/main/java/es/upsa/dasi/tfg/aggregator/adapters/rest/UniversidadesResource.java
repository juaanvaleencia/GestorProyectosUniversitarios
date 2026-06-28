package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UniversidadResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Universidades", description = "Catálogo de universidades")
@Path("/api/universidades")
@Produces(MediaType.APPLICATION_JSON)
public class UniversidadesResource
{
    Repository repository;

    @Inject
    public UniversidadesResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public List<UniversidadResponse> list() throws TfgException
    {
        return repository.findUniversidades();
    }

    @GET
    @Path("/{universidadId}/asignaturas")
    public List<AsignaturaResponse> asignaturas(@PathParam("universidadId") long universidadId) throws TfgException
    {
        return repository.findAsignaturasByUniversidad(universidadId);
    }
}
