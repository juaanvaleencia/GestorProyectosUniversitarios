package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class TareasResource
{
    Repository repository;

    @Inject
    public TareasResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    @Path("proyectos/{proyectoId}/tareas")
    public List<TareaResponse> list(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return repository.findTareasByProyecto(proyectoId);
    }
}
