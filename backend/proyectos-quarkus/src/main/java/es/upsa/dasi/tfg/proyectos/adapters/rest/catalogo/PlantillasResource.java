package es.upsa.dasi.tfg.proyectos.adapters.rest.catalogo;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreatePlantillaProyectoRequest;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaProyectoDetalleResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaProyectoResponse;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.CreatePlantillaProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.UpdatePlantillaProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.GetPlantillaByIdUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.ListPlantillasByAsignaturaUsecase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Plantillas", description = "Plantillas de proyecto por asignatura")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class PlantillasResource
{
    @Inject ListPlantillasByAsignaturaUsecase listPlantillas;
    @Inject GetPlantillaByIdUsecase getPlantillaById;
    @Inject CreatePlantillaProyectoUsecase createPlantilla;
    @Inject UpdatePlantillaProyectoUsecase updatePlantilla;
    @Inject ResponseMappers mappers;

    @GET
    @Path("/asignaturas/{asignaturaId}/plantillas")
    public List<PlantillaProyectoResponse> listByAsignatura(@PathParam("asignaturaId") long asignaturaId)
            throws NotFoundTfgException
    {
        return listPlantillas.execute(asignaturaId).stream().map(mappers::toPlantillaProyectoResponse).toList();
    }

    @POST
    @Path("/asignaturas/{asignaturaId}/plantillas")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createByAsignatura(
            @PathParam("asignaturaId") long asignaturaId,
            @Valid CreatePlantillaProyectoRequest request) throws NotFoundTfgException
    {
        var plantilla = createPlantilla.execute(asignaturaId, request);
        return Response.ok(mappers.toPlantillaProyectoDetalleResponse(plantilla)).build();
    }

    @PUT
    @Path("/plantillas/{plantillaId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("plantillaId") long plantillaId,
            @Valid CreatePlantillaProyectoRequest request) throws NotFoundTfgException
    {
        var plantilla = updatePlantilla.execute(plantillaId, request);
        return Response.ok(mappers.toPlantillaProyectoDetalleResponse(plantilla)).build();
    }

    @GET
    @Path("/plantillas/{plantillaId}")
    public PlantillaProyectoDetalleResponse get(@PathParam("plantillaId") long plantillaId)
            throws NotFoundTfgException
    {
        return mappers.toPlantillaProyectoDetalleResponse(getPlantillaById.execute(plantillaId));
    }
}
