package es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto.ProyectoPostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto.ProyectoPutRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.CreateProyectoDesdePlantillaUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.CreateProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.GetProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.ListProyectosUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.RemoveProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.UpdateProyectoUsecase;
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

@Tag(name = "Proyectos", description = "CRUD y consulta de proyectos universitarios")
@Path("/api/proyectos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProyectosResource
{
    ResponseMappers mapper;
    ListProyectosUsecase listProyectos;
    CreateProyectoUsecase createProyecto;
    CreateProyectoDesdePlantillaUsecase createProyectoDesdePlantilla;
    GetProyectoByIdUsecase getProyectoById;
    UpdateProyectoUsecase updateProyecto;
    RemoveProyectoByIdUsecase removeProyectoById;
    Validator validator;

    @Inject
    public ProyectosResource(
            ResponseMappers mapper,
            ListProyectosUsecase listProyectos,
            CreateProyectoUsecase createProyecto,
            CreateProyectoDesdePlantillaUsecase createProyectoDesdePlantilla,
            GetProyectoByIdUsecase getProyectoById,
            UpdateProyectoUsecase updateProyecto,
            RemoveProyectoByIdUsecase removeProyectoById,
            Validator validator)
    {
        this.mapper = mapper;
        this.listProyectos = listProyectos;
        this.createProyecto = createProyecto;
        this.createProyectoDesdePlantilla = createProyectoDesdePlantilla;
        this.getProyectoById = getProyectoById;
        this.updateProyecto = updateProyecto;
        this.removeProyectoById = removeProyectoById;
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

    @POST
    @Path("desde-plantilla/{plantillaId}")
    public Response createFromPlantilla(@PathParam("plantillaId") long plantillaId, @Context UriInfo uriInfo)
            throws NotFoundTfgException
    {
        Proyecto proyecto = createProyectoDesdePlantilla.execute(plantillaId);
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
}
