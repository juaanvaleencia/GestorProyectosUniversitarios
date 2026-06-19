package es.upsa.dasi.tfg.aggregator.infrastructure.rest.usuarios;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
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
    @Path("/notificaciones")
    @Produces(MediaType.APPLICATION_JSON)
    List<NotificacionResponse> listNotificaciones() throws TfgException;
}
