package es.upsa.dasi.tfg.tareas.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.adapters.rest.dtos.TareaPostRequest;
import es.upsa.dasi.tfg.tareas.adapters.rest.dtos.TareaPutRequest;
import es.upsa.dasi.tfg.tareas.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.tareas.application.usecases.AddTareaUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.FindTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.ListTareasByProyectoUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.RemoveTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.ReplaceTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
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

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TareasResource
{
    ResponseMappers mapper;
    ListTareasByProyectoUsecase listTareasByProyecto;
    AddTareaUsecase addTarea;
    FindTareaByIdUsecase findTareaById;
    ReplaceTareaByIdUsecase replaceTareaById;
    RemoveTareaByIdUsecase removeTareaById;
    Validator validator;

    @Inject
    public TareasResource(
            ResponseMappers mapper,
            ListTareasByProyectoUsecase listTareasByProyecto,
            AddTareaUsecase addTarea,
            FindTareaByIdUsecase findTareaById,
            ReplaceTareaByIdUsecase replaceTareaById,
            RemoveTareaByIdUsecase removeTareaById,
            Validator validator)
    {
        this.mapper = mapper;
        this.listTareasByProyecto = listTareasByProyecto;
        this.addTarea = addTarea;
        this.findTareaById = findTareaById;
        this.replaceTareaById = replaceTareaById;
        this.removeTareaById = removeTareaById;
        this.validator = validator;
    }

    @GET
    @Path("proyectos/{proyectoId}/tareas")
    public List<TareaResponse> listByProyecto(@PathParam("proyectoId") long proyectoId)
    {
        return listTareasByProyecto.execute(proyectoId).stream().map(mapper::toResponse).toList();
    }

    @POST
    @Path("proyectos/{proyectoId}/tareas")
    public Response create(
            @PathParam("proyectoId") long proyectoId,
            TareaPostRequest request,
            @Context UriInfo uriInfo)
    {
        Set<ConstraintViolation<TareaPostRequest>> violationSet = validator.validate(request);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        Tarea tarea = addTarea.execute(proyectoId, mapper.toAddTareaCommand(request));
        var fullResponse = mapper.toTareaFullResponse(tarea, uriInfo);
        return Response.created(fullResponse.getUri())
                .entity(fullResponse)
                .build();
    }

    @GET
    @Path("proyectos/{proyectoId}/tareas/{id}")
    public Response get(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id,
            @Context UriInfo uriInfo) throws NotFoundTfgException
    {
        return findTareaById.execute(id)
                .filter(t -> t.getProyectoId() == proyectoId)
                .map(t -> mapper.toTareaFullResponse(t, uriInfo))
                .map(fullResponse -> Response.ok().entity(fullResponse).build())
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
    }

    @PUT
    @Path("proyectos/{proyectoId}/tareas/{id}")
    public Response update(
            @PathParam("proyectoId") long proyectoId,
            @PathParam("id") long id,
            @Valid TareaPutRequest request) throws NotFoundTfgException
    {
        findTareaById.execute(id)
                .filter(t -> t.getProyectoId() == proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        replaceTareaById.execute(id, mapper.toReplaceTareaCommand(request));
        return Response.noContent().build();
    }

    @DELETE
    @Path("proyectos/{proyectoId}/tareas/{id}")
    public Response delete(@PathParam("proyectoId") long proyectoId, @PathParam("id") long id) throws NotFoundTfgException
    {
        findTareaById.execute(id)
                .filter(t -> t.getProyectoId() == proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        removeTareaById.execute(id);
        return Response.noContent().build();
    }
}
