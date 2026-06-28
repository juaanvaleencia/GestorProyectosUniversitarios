package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.MiembroInvite;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.InvitacionProyectoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.MiembroResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Miembros", description = "Gestión de miembros del equipo de un proyecto")
@Path("/api/proyectos/{proyectoId}/miembros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MiembrosResource
{
    Repository repository;

    @Inject
    public MiembrosResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<MiembroResponse> list(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return repository.findMiembrosByProyecto(proyectoId);
    }

    @POST
    public Response invite(
            @PathParam("proyectoId") long proyectoId,
            @Valid MiembroInvite request,
            @Context UriInfo uriInfo) throws TfgException
    {
        InvitacionProyectoResponse created = repository.inviteMiembro(proyectoId, request);
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/invitaciones/{id}")
                        .resolveTemplate("id", created.getId())
                        .build())
                .entity(created)
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws TfgException {
        repository.removeMiembroById(proyectoId, id);
        return Response.noContent().build();
    }

    @POST
    @Path("abandonar")
    public Response abandonar(@PathParam("proyectoId") long proyectoId) throws TfgException {
        repository.abandonProyecto(proyectoId);
        return Response.noContent().build();
    }
}
