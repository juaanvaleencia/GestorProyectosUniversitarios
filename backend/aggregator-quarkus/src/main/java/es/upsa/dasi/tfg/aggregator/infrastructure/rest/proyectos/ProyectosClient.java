package es.upsa.dasi.tfg.aggregator.infrastructure.rest.proyectos;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.MiembroInvite;
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

    @POST
    @Path("/proyectos/desde-plantilla/{plantillaId}")
    @Produces(MediaType.APPLICATION_JSON)
    ProyectoFullResponse createFromPlantilla(@PathParam("plantillaId") long plantillaId) throws TfgException;

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

    @POST
    @Path("/proyectos/{id}/hitos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    HitoResponse createHito(@PathParam("id") long id, HitoPost request) throws TfgException;

    @PUT
    @Path("/proyectos/{id}/hitos/{hitoId}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateHito(@PathParam("id") long id, @PathParam("hitoId") long hitoId, HitoPut request) throws TfgException;

    @DELETE
    @Path("/proyectos/{id}/hitos/{hitoId}")
    Response removeHito(@PathParam("id") long id, @PathParam("hitoId") long hitoId) throws TfgException;

    @GET
    @Path("/proyectos/{id}/miembros")
    @Produces(MediaType.APPLICATION_JSON)
    List<MiembroResponse> miembros(@PathParam("id") long id) throws TfgException;

    @POST
    @Path("/proyectos/{id}/miembros")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    InvitacionProyectoResponse inviteMiembro(@PathParam("id") long id, MiembroInvite request) throws TfgException;

    @DELETE
    @Path("/proyectos/{id}/miembros/{miembroId}")
    Response removeMiembro(@PathParam("id") long id, @PathParam("miembroId") long miembroId) throws TfgException;

    @POST
    @Path("/proyectos/{id}/miembros/abandonar")
    Response abandonProyecto(@PathParam("id") long id) throws TfgException;

    @GET
    @Path("/proyectos/{id}/invitaciones")
    @Produces(MediaType.APPLICATION_JSON)
    List<InvitacionProyectoResponse> invitaciones(@PathParam("id") long id) throws TfgException;

    @POST
    @Path("/invitaciones/{invitacionId}/aceptar")
    Response acceptInvitacion(@PathParam("invitacionId") long invitacionId) throws TfgException;

    @POST
    @Path("/invitaciones/{invitacionId}/rechazar")
    Response rejectInvitacion(@PathParam("invitacionId") long invitacionId) throws TfgException;

    @GET
    @Path("/universidades/{universidadId}/asignaturas")
    @Produces(MediaType.APPLICATION_JSON)
    List<AsignaturaResponse> asignaturas(@PathParam("universidadId") long universidadId) throws TfgException;

    @GET
    @Path("/asignaturas/{asignaturaId}/plantillas")
    @Produces(MediaType.APPLICATION_JSON)
    List<PlantillaProyectoResponse> plantillas(@PathParam("asignaturaId") long asignaturaId) throws TfgException;

    @PUT
    @Path("/plantillas/{plantillaId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PlantillaProyectoDetalleResponse updatePlantilla(
            @PathParam("plantillaId") long plantillaId,
            CreatePlantillaProyectoRequest request) throws TfgException;

    @GET
    @Path("/plantillas/{plantillaId}")
    @Produces(MediaType.APPLICATION_JSON)
    PlantillaProyectoDetalleResponse plantilla(@PathParam("plantillaId") long plantillaId) throws TfgException;

    @POST
    @Path("/asignaturas/{asignaturaId}/plantillas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PlantillaProyectoDetalleResponse createPlantilla(
            @PathParam("asignaturaId") long asignaturaId,
            CreatePlantillaProyectoRequest request) throws TfgException;

    @GET
    @Path("/profesor/asignaturas/{asignaturaId}/proyectos")
    @Produces(MediaType.APPLICATION_JSON)
    List<ProyectoSupervisionResponse> proyectosSupervision(@PathParam("asignaturaId") long asignaturaId)
            throws TfgException;

    @GET
    @Path("/profesor/plantillas/{plantillaId}/grupos")
    @Produces(MediaType.APPLICATION_JSON)
    List<ProyectoGrupoSupervisionResponse> gruposPorPlantilla(@PathParam("plantillaId") long plantillaId)
            throws TfgException;

    @GET
    @Path("/profesor/alumnos/{alumnoUid}/participaciones")
    @Produces(MediaType.APPLICATION_JSON)
    List<ProyectoParticipacionResponse> participacionesAlumno(@PathParam("alumnoUid") String alumnoUid)
            throws TfgException;
}
