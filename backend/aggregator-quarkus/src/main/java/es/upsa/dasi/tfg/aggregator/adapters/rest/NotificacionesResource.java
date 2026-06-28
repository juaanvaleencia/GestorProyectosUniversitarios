package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Notificaciones", description = "Notificaciones e invitaciones del usuario")
@Path("/api/notificaciones")
@Produces(MediaType.APPLICATION_JSON)
public class NotificacionesResource
{
    Repository repository;

    @Inject
    public NotificacionesResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<NotificacionResponse> list() throws TfgException {
        return repository.findNotificaciones();
    }
}
