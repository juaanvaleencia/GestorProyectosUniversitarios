package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreatePlantillaProyectoRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaProyectoDetalleResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaProyectoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Asignaturas", description = "Plantillas asociadas a una asignatura")
@Path("/api/asignaturas/{asignaturaId}/plantillas")
@Produces(MediaType.APPLICATION_JSON)
public class AsignaturaPlantillasResource
{
    Repository repository;

    @Inject
    public AsignaturaPlantillasResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public List<PlantillaProyectoResponse> list(@PathParam("asignaturaId") long asignaturaId) throws TfgException
    {
        return repository.findPlantillasByAsignatura(asignaturaId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public PlantillaProyectoDetalleResponse create(
            @PathParam("asignaturaId") long asignaturaId,
            @Valid CreatePlantillaProyectoRequest request) throws TfgException
    {
        return repository.createPlantilla(asignaturaId, request);
    }
}
