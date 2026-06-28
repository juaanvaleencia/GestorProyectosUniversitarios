package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
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
    Repository repository;

    @Inject
    public ProfesorProyectosResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public List<ProyectoSupervisionResponse> list(@PathParam("asignaturaId") long asignaturaId) throws TfgException
    {
        return repository.findProyectosSupervisionByAsignatura(asignaturaId);
    }
}
