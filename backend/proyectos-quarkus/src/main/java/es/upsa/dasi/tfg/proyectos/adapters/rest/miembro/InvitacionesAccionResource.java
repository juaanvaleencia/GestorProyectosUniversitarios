package es.upsa.dasi.tfg.proyectos.adapters.rest.miembro;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.AcceptInvitacionUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.RejectInvitacionUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Invitaciones", description = "Aceptar o rechazar invitaciones a proyectos")
@Path("/api/invitaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvitacionesAccionResource
{
    AcceptInvitacionUsecase acceptInvitacion;
    RejectInvitacionUsecase rejectInvitacion;

    @Inject
    public InvitacionesAccionResource(
            AcceptInvitacionUsecase acceptInvitacion,
            RejectInvitacionUsecase rejectInvitacion)
    {
        this.acceptInvitacion = acceptInvitacion;
        this.rejectInvitacion = rejectInvitacion;
    }

    @POST
    @Path("{id}/aceptar")
    public Response accept(@PathParam("id") long id) throws NotFoundTfgException
    {
        acceptInvitacion.execute(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/rechazar")
    public Response reject(@PathParam("id") long id) throws NotFoundTfgException
    {
        rejectInvitacion.execute(id);
        return Response.noContent().build();
    }
}
