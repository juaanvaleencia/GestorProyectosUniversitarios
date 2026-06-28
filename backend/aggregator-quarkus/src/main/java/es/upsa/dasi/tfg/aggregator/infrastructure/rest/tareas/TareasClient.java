package es.upsa.dasi.tfg.aggregator.infrastructure.rest.tareas;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPut;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaFullResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "tareas-api")
@RegisterClientHeaders(ForwardAuthorizationFactory.class)
@RegisterProvider(TfgResponseExceptionMapper.class)
@Path("/api")
public interface TareasClient
{
    @GET
    @Path("/proyectos/{proyectoId}/tareas")
    @Produces(MediaType.APPLICATION_JSON)
    List<TareaResponse> listByProyecto(@PathParam("proyectoId") long proyectoId) throws TfgException;

    @POST
    @Path("/proyectos/{proyectoId}/tareas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TareaFullResponse create(@PathParam("proyectoId") long proyectoId, TareaPost request) throws TfgException;

    @GET
    @Path("/proyectos/{proyectoId}/tareas/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    TareaFullResponse get(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws TfgException;

    @PUT
    @Path("/proyectos/{proyectoId}/tareas/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response update(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id, TareaPut request)
            throws TfgException;

    @DELETE
    @Path("/proyectos/{proyectoId}/tareas/{id}")
    Response delete(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws TfgException;
}
