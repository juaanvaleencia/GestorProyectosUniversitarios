package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("/api/proyectos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProyectosResource
{
    Repository repository;

    @Inject
    public ProyectosResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<ProyectoResponse> list() throws TfgException {
        return repository.findProyectos();
    }

    @POST
    public Response create(@Valid ProyectoPost request, @Context UriInfo uriInfo) throws TfgException {
        Proyecto proyecto = repository.createProyecto(request);
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/proyectos/{id}")
                        .resolveTemplate("id", proyecto.getId())
                        .build())
                .entity(proyecto)
                .build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) throws TfgException {
        return repository.findProyectoById(id)
                .map(proyecto -> Response.ok().entity(proyecto).build())
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + id));
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, @Valid ProyectoPut request) throws TfgException {
        repository.updateProyecto(id, request);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) throws TfgException {
        repository.removeProyectoById(id);
        return Response.noContent().build();
    }

    @GET
    @Path("{proyectoId}/hitos")
    public Response hitos(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return Response.ok(repository.findHitosByProyecto(proyectoId)).build();
    }

    @GET
    @Path("{proyectoId}/miembros")
    public Response miembros(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return Response.ok(repository.findMiembrosByProyecto(proyectoId)).build();
    }
}
