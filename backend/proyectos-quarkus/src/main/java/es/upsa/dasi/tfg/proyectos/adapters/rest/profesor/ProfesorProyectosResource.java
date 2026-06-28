package es.upsa.dasi.tfg.proyectos.adapters.rest.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListProyectosSupervisionByAsignaturaUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Profesor", description = "Vista del profesor sobre proyectos de sus asignaturas")
@Path("/api/profesor/asignaturas/{asignaturaId}/proyectos")
@Produces(MediaType.APPLICATION_JSON)
public class ProfesorProyectosResource
{
    @Inject ListProyectosSupervisionByAsignaturaUsecase listProyectos;
    @Inject ResponseMappers mappers;

    @GET
    public List<ProyectoSupervisionResponse> list(@PathParam("asignaturaId") long asignaturaId)
            throws NotFoundTfgException
    {
        return listProyectos.execute(asignaturaId).stream()
                .map(mappers::toProyectoSupervisionResponse)
                .toList();
    }
}
