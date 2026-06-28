package es.upsa.dasi.tfg.proyectos.adapters.rest.miembro;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.InvitacionProyectoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.MiembroResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.adapters.rest.miembro.MiembroInvitePostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.AbandonProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.InviteMiembroUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.ListMiembrosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.RemoveMiembroByIdUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Set;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Miembros", description = "Gestión de miembros del equipo de un proyecto")
@Path("/api/proyectos/{proyectoId}/miembros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MiembrosResource
{
    ResponseMappers mapper;
    ListMiembrosByProyectoUsecase listMiembros;
    InviteMiembroUsecase inviteMiembro;
    RemoveMiembroByIdUsecase removeMiembroById;
    AbandonProyectoUsecase abandonProyecto;
    Validator validator;

    @Inject
    public MiembrosResource(
            ResponseMappers mapper,
            ListMiembrosByProyectoUsecase listMiembros,
            InviteMiembroUsecase inviteMiembro,
            RemoveMiembroByIdUsecase removeMiembroById,
            AbandonProyectoUsecase abandonProyecto,
            Validator validator)
    {
        this.mapper = mapper;
        this.listMiembros = listMiembros;
        this.inviteMiembro = inviteMiembro;
        this.removeMiembroById = removeMiembroById;
        this.abandonProyecto = abandonProyecto;
        this.validator = validator;
    }

    @GET
    public List<MiembroResponse> list(@PathParam("proyectoId") long proyectoId)
    {
        return listMiembros.execute(proyectoId).stream().map(mapper::toResponse).toList();
    }

    @POST
    public Response invite(
            @PathParam("proyectoId") long proyectoId,
            MiembroInvitePostRequest request,
            @Context UriInfo uriInfo) throws NotFoundTfgException
    {
        Set<ConstraintViolation<MiembroInvitePostRequest>> violationSet = validator.validate(request);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        InvitacionProyecto invitacion = inviteMiembro.execute(proyectoId, mapper.toInviteMiembroCommand(request));
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/invitaciones/{id}")
                        .resolveTemplate("id", invitacion.getId())
                        .build())
                .entity(mapper.toResponse(invitacion))
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws NotFoundTfgException
    {
        removeMiembroById.execute(proyectoId, id);
        return Response.noContent().build();
    }

    @POST
    @Path("abandonar")
    public Response abandonar(@PathParam("proyectoId") long proyectoId) throws NotFoundTfgException
    {
        abandonProyecto.execute(proyectoId);
        return Response.noContent().build();
    }
}
