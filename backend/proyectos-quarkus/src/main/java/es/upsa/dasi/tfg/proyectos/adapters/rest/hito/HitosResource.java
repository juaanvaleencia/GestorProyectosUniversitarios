package es.upsa.dasi.tfg.proyectos.adapters.rest.hito;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.HitoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.CreateHitoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.ListHitosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.RemoveHitoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.UpdateHitoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Set;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Hitos", description = "Hitos y fechas clave de proyectos")
@Path("/api/proyectos/{proyectoId}/hitos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HitosResource
{
    ResponseMappers mapper;
    ListHitosByProyectoUsecase listHitos;
    CreateHitoUsecase createHito;
    UpdateHitoUsecase updateHito;
    RemoveHitoByIdUsecase removeHitoById;
    Validator validator;

    @Inject
    public HitosResource(
            ResponseMappers mapper,
            ListHitosByProyectoUsecase listHitos,
            CreateHitoUsecase createHito,
            UpdateHitoUsecase updateHito,
            RemoveHitoByIdUsecase removeHitoById,
            Validator validator)
    {
        this.mapper = mapper;
        this.listHitos = listHitos;
        this.createHito = createHito;
        this.updateHito = updateHito;
        this.removeHitoById = removeHitoById;
        this.validator = validator;
    }

    @GET
    public List<HitoResponse> list(@PathParam("proyectoId") long proyectoId)
    {
        return listHitos.execute(proyectoId).stream().map(mapper::toResponse).toList();
    }

    @POST
    public Response create(
            @PathParam("proyectoId") long proyectoId,
            HitoPostRequest request,
            @Context UriInfo uriInfo) throws NotFoundTfgException
    {
        Set<ConstraintViolation<HitoPostRequest>> violationSet = validator.validate(request);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        Hito hito = createHito.execute(proyectoId, mapper.toAddHitoCommand(request));
        return Response.created(uriInfo.getBaseUriBuilder()
                        .path("/api/proyectos/{proyectoId}/hitos/{id}")
                        .resolveTemplate("proyectoId", proyectoId)
                        .resolveTemplate("id", hito.getId())
                        .build())
                .entity(mapper.toResponse(hito))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id,
            @Valid HitoPutRequest request) throws NotFoundTfgException
    {
        updateHito.execute(proyectoId, id, mapper.toReplaceHitoCommand(request));
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws NotFoundTfgException
    {
        removeHitoById.execute(proyectoId, id);
        return Response.noContent().build();
    }
}
