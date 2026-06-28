package es.upsa.dasi.tfg.usuarios.adapters.rest.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoMatriculadoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoPerfilSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.usuarios.application.usecases.profesor.GetAlumnoPerfilSupervisionUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.profesor.ListAlumnosMatriculadosUsecase;
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
    @Inject ListAlumnosMatriculadosUsecase listAlumnos;
    @Inject GetAlumnoPerfilSupervisionUsecase getAlumnoPerfil;

    @GET
    public List<AlumnoMatriculadoResponse> list() throws ForbiddenTfgException
    {
        return listAlumnos.execute();
    }

    @GET
    @Path("{alumnoUid}")
    public AlumnoPerfilSupervisionResponse get(@PathParam("alumnoUid") String alumnoUid)
            throws ForbiddenTfgException, NotFoundTfgException
    {
        return getAlumnoPerfil.execute(alumnoUid);
    }
}
