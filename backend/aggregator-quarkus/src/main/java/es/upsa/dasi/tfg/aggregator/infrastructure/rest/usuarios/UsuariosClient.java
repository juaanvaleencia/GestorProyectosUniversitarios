package es.upsa.dasi.tfg.aggregator.infrastructure.rest.usuarios;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ActualizarMatriculasRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoMatriculadoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoPerfilSupervisionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreateAsignaturaProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.RegistroProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UniversidadResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "usuarios-api")
@RegisterClientHeaders(ForwardAuthorizationFactory.class)
@RegisterProvider(TfgResponseExceptionMapper.class)
@Path("/api")
public interface UsuariosClient
{
    @GET
    @Path("/usuarios/perfil")
    @Produces(MediaType.APPLICATION_JSON)
    UsuarioPerfilResponse perfil() throws TfgException;

    @POST
    @Path("/usuarios/sync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response sync(@Valid UsuarioSync request) throws TfgException;

    @GET
    @Path("/usuarios/mis-asignaturas")
    @Produces(MediaType.APPLICATION_JSON)
    List<AsignaturaResponse> misAsignaturas() throws TfgException;

    @PUT
    @Path("/usuarios/mis-asignaturas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<AsignaturaResponse> actualizarMisAsignaturas(@Valid ActualizarMatriculasRequest request) throws TfgException;

    @POST
    @Path("/usuarios/asignaturas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response crearAsignatura(@Valid CreateAsignaturaProfesorRequest request) throws TfgException;

    @POST
    @Path("/usuarios/registro-profesor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response registroProfesor(@Valid RegistroProfesorRequest request) throws TfgException;

    @GET
    @Path("/notificaciones")
    @Produces(MediaType.APPLICATION_JSON)
    List<NotificacionResponse> listNotificaciones() throws TfgException;

    @GET
    @Path("/universidades")
    @Produces(MediaType.APPLICATION_JSON)
    List<UniversidadResponse> listUniversidades() throws TfgException;

    @GET
    @Path("/profesor/alumnos")
    @Produces(MediaType.APPLICATION_JSON)
    List<AlumnoMatriculadoResponse> listAlumnosMatriculados() throws TfgException;

    @GET
    @Path("/profesor/alumnos/{alumnoUid}")
    @Produces(MediaType.APPLICATION_JSON)
    AlumnoPerfilSupervisionResponse alumnoPerfil(@PathParam("alumnoUid") String alumnoUid) throws TfgException;
}
