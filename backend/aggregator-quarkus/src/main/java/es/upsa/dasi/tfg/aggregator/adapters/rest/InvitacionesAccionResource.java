package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
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
    Repository repository;

    @Inject
    public InvitacionesAccionResource(Repository repository) {
        this.repository = repository;
    }

    @POST
    @Path("{id}/aceptar")
    public Response accept(@PathParam("id") long id) throws TfgException {
        repository.acceptInvitacion(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/rechazar")
    public Response reject(@PathParam("id") long id) throws TfgException {
        repository.rejectInvitacion(id);
        return Response.noContent().build();
    }
}
