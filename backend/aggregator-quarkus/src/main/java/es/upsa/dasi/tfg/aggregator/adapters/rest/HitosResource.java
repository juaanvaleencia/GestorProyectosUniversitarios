package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPut;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.HitoResponse;
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

@Tag(name = "Hitos", description = "Hitos y fechas clave de proyectos")
@Path("/api/proyectos/{proyectoId}/hitos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HitosResource
{
    Repository repository;

    @Inject
    public HitosResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<HitoResponse> list(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return repository.findHitosByProyecto(proyectoId);
    }

    @POST
    public Response create(
            @PathParam("proyectoId") long proyectoId,
            @Valid HitoPost request,
            @Context UriInfo uriInfo) throws TfgException
    {
        HitoResponse created = repository.createHito(proyectoId, request);
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/proyectos/{proyectoId}/hitos/{id}")
                        .resolveTemplate("proyectoId", proyectoId)
                        .resolveTemplate("id", created.getId())
                        .build())
                .entity(created)
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id,
            @Valid HitoPut request) throws TfgException
    {
        repository.updateHito(proyectoId, id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws TfgException {
        repository.removeHitoById(proyectoId, id);
        return Response.noContent().build();
    }
}
