package es.upsa.dasi.tfg.usuarios.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
import es.upsa.dasi.tfg.usuarios.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListNotificacionesUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/notificaciones")
@Produces(MediaType.APPLICATION_JSON)
public class NotificacionesResource
{
    ListNotificacionesUsecase listNotificacionesUsecase;
    ResponseMappers responseMappers;

    @Inject
    public NotificacionesResource(ListNotificacionesUsecase listNotificacionesUsecase, ResponseMappers responseMappers)
    {
        this.listNotificacionesUsecase = listNotificacionesUsecase;
        this.responseMappers = responseMappers;
    }

    @GET
    public List<NotificacionResponse> list()
    {
        return listNotificacionesUsecase.execute().stream()
                .map(responseMappers::toNotificacionResponse)
                .toList();
    }
}
