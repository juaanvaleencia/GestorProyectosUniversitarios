package es.upsa.dasi.tfg.aggregator.infrastructure.rest.proyectos;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "proyectos-api")
@RegisterClientHeaders(ForwardAuthorizationFactory.class)
@RegisterProvider(TfgResponseExceptionMapper.class)
@Path("/api")
public interface ProyectosClient
{
    @GET
    @Path("/usuarios/me/participaciones")
    @Produces(MediaType.APPLICATION_JSON)
    List<ProyectoParticipacionResponse> participaciones() throws TfgException;

    @GET
    @Path("/proyectos")
    @Produces(MediaType.APPLICATION_JSON)
    List<ProyectoResponse> list() throws TfgException;

    @POST
    @Path("/proyectos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ProyectoFullResponse create(ProyectoPost request) throws TfgException;

    @GET
    @Path("/proyectos/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ProyectoFullResponse get(@PathParam("id") long id) throws TfgException;

    @PUT
    @Path("/proyectos/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response update(@PathParam("id") long id, ProyectoPut request) throws TfgException;

    @DELETE
    @Path("/proyectos/{id}")
    Response delete(@PathParam("id") long id) throws TfgException;

    @GET
    @Path("/proyectos/{id}/hitos")
    @Produces(MediaType.APPLICATION_JSON)
    List<HitoResponse> hitos(@PathParam("id") long id) throws TfgException;

    @GET
    @Path("/proyectos/{id}/miembros")
    @Produces(MediaType.APPLICATION_JSON)
    List<MiembroResponse> miembros(@PathParam("id") long id) throws TfgException;
}
