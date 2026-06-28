package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPut;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaFullResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Tareas", description = "Tareas y subtareas de proyectos")
@Path("/api/proyectos/{proyectoId}/tareas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TareasResource
{
    Repository repository;

    @Inject
    public TareasResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<TareaResponse> list(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return repository.findTareasByProyecto(proyectoId);
    }

    @POST
    public Response create(
            @PathParam("proyectoId") long proyectoId,
            @Valid TareaPost request,
            @Context UriInfo uriInfo) throws TfgException
    {
        TareaFullResponse created = repository.createTarea(proyectoId, request);
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/proyectos/{proyectoId}/tareas/{id}")
                        .resolveTemplate("proyectoId", proyectoId)
                        .resolveTemplate("id", created.getId())
                        .build())
                .entity(created)
                .build();
    }

    @GET
    @Path("{id}")
    public Response get(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id) throws TfgException
    {
        return repository.findTareaById(proyectoId, id)
                .map(tarea -> Response.ok().entity(tarea).build())
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id,
            @Valid TareaPut request) throws TfgException
    {
        repository.findTareaById(proyectoId, id)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        repository.updateTarea(proyectoId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws TfgException {
        repository.findTareaById(proyectoId, id)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        repository.removeTareaById(proyectoId, id);
        return Response.noContent().build();
    }
}
