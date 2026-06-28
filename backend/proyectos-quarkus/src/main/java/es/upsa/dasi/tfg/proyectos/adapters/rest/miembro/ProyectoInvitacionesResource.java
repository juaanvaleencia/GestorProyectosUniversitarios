package es.upsa.dasi.tfg.proyectos.adapters.rest.miembro;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.InvitacionProyectoResponse;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.ListInvitacionesByProyectoUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Invitaciones", description = "Invitar miembros a un proyecto")
@Path("/api/proyectos/{proyectoId}/invitaciones")
@Produces(MediaType.APPLICATION_JSON)
public class ProyectoInvitacionesResource
{
    ResponseMappers mapper;
    ListInvitacionesByProyectoUsecase listInvitaciones;

    @Inject
    public ProyectoInvitacionesResource(
            ResponseMappers mapper,
            ListInvitacionesByProyectoUsecase listInvitaciones)
    {
        this.mapper = mapper;
        this.listInvitaciones = listInvitaciones;
    }

    @GET
    public List<InvitacionProyectoResponse> list(@PathParam("proyectoId") long proyectoId)
    {
        return listInvitaciones.execute(proyectoId).stream().map(mapper::toResponse).toList();
    }
}
