package es.upsa.dasi.tfg.aggregator.infrastructure.rest.tareas;

import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "tareas-api")
@RegisterClientHeaders(ForwardAuthorizationFactory.class)
@RegisterProvider(TfgResponseExceptionMapper.class)
public interface TareasClient
{
    @GET
    @Path("/api/proyectos/{proyectoId}/tareas")
    @Produces(MediaType.APPLICATION_JSON)
    List<TareaResponse> listByProyecto(@PathParam("proyectoId") long proyectoId) throws TfgException;
}
