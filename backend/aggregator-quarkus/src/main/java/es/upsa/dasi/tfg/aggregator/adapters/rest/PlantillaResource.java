package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreatePlantillaProyectoRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaProyectoDetalleResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Plantillas", description = "Plantillas de proyecto (agregador)")
@Path("/api/plantillas/{plantillaId}")
@Produces(MediaType.APPLICATION_JSON)
public class PlantillaResource
{
    Repository repository;

    @Inject
    public PlantillaResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public PlantillaProyectoDetalleResponse get(@PathParam("plantillaId") long plantillaId) throws TfgException
    {
        return repository.findPlantillaById(plantillaId);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public PlantillaProyectoDetalleResponse update(
            @PathParam("plantillaId") long plantillaId,
            @Valid CreatePlantillaProyectoRequest request) throws TfgException
    {
        return repository.updatePlantilla(plantillaId, request);
    }
}
