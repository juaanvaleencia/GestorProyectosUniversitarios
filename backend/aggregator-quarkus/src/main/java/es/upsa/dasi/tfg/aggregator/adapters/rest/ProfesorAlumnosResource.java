package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoMatriculadoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoPerfilSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Profesor", description = "Alumnos matriculados en asignaturas del profesor")
@Path("/api/profesor/alumnos")
@Produces(MediaType.APPLICATION_JSON)
public class ProfesorAlumnosResource
{
    Repository repository;

    @Inject
    public ProfesorAlumnosResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public List<AlumnoMatriculadoResponse> list() throws TfgException
    {
        return repository.findAlumnosMatriculados();
    }

    @GET
    @Path("{alumnoUid}")
    public AlumnoPerfilSupervisionResponse get(@PathParam("alumnoUid") String alumnoUid) throws TfgException
    {
        return repository.findAlumnoPerfil(alumnoUid);
    }
}
