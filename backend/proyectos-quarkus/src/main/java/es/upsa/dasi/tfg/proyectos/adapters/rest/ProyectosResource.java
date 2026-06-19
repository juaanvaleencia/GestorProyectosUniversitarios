package es.upsa.dasi.tfg.proyectos.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.HitoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.MiembroResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.adapters.rest.dtos.ProyectoPostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.dtos.ProyectoPutRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.CreateProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.GetProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.ListHitosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.ListMiembrosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.ListProyectosUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.RemoveProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.UpdateProyectoUsecase;
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

@Path("/api/proyectos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProyectosResource
{
    ResponseMappers mapper;
    ListProyectosUsecase listProyectos;
    CreateProyectoUsecase createProyecto;
    GetProyectoByIdUsecase getProyectoById;
    UpdateProyectoUsecase updateProyecto;
    RemoveProyectoByIdUsecase removeProyectoById;
    ListHitosByProyectoUsecase listHitos;
    ListMiembrosByProyectoUsecase listMiembros;
    Validator validator;

    @Inject
    public ProyectosResource(
            ResponseMappers mapper,
            ListProyectosUsecase listProyectos,
            CreateProyectoUsecase createProyecto,
            GetProyectoByIdUsecase getProyectoById,
            UpdateProyectoUsecase updateProyecto,
            RemoveProyectoByIdUsecase removeProyectoById,
            ListHitosByProyectoUsecase listHitos,
            ListMiembrosByProyectoUsecase listMiembros,
            Validator validator)
    {
        this.mapper = mapper;
        this.listProyectos = listProyectos;
        this.createProyecto = createProyecto;
        this.getProyectoById = getProyectoById;
        this.updateProyecto = updateProyecto;
        this.removeProyectoById = removeProyectoById;
        this.listHitos = listHitos;
        this.listMiembros = listMiembros;
        this.validator = validator;
    }

    @GET
    public List<ProyectoResponse> list()
    {
        return listProyectos.execute().stream().map(mapper::toResponse).toList();
    }

    @POST
    public Response create(ProyectoPostRequest request, @Context UriInfo uriInfo)
    {
        Set<ConstraintViolation<ProyectoPostRequest>> violationSet = validator.validate(request);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        Proyecto proyecto = createProyecto.execute(mapper.toAddProyectoCommand(request));
        var fullResponse = mapper.toProyectoFullResponse(proyecto, uriInfo);
        return Response.created(fullResponse.getUri())
                .entity(fullResponse)
                .build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id, @Context UriInfo uriInfo) throws NotFoundTfgException
    {
        return getProyectoById.execute(id)
                .map(proyecto -> mapper.toProyectoFullResponse(proyecto, uriInfo))
                .map(fullResponse -> Response.ok().entity(fullResponse).build())
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + id));
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, @Valid ProyectoPutRequest request) throws NotFoundTfgException
    {
        updateProyecto.execute(id, mapper.toReplaceProyectoCommand(request));
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) throws NotFoundTfgException
    {
        removeProyectoById.execute(id);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/hitos")
    public List<HitoResponse> hitos(@PathParam("id") long id)
    {
        return listHitos.execute(id).stream().map(mapper::toResponse).toList();
    }

    @GET
    @Path("{id}/miembros")
    public List<MiembroResponse> miembros(@PathParam("id") long id)
    {
        return listMiembros.execute(id).stream().map(mapper::toResponse).toList();
    }
}
