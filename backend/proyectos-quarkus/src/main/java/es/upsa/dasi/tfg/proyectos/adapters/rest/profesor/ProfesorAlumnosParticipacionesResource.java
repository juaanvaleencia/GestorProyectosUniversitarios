package es.upsa.dasi.tfg.proyectos.adapters.rest.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoParticipacionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListParticipacionesAlumnoForProfesorUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Profesor", description = "Participaciones de alumnos en proyectos")
@Path("/api/profesor/alumnos/{alumnoUid}/participaciones")
@Produces(MediaType.APPLICATION_JSON)
public class ProfesorAlumnosParticipacionesResource
{
    @Inject ListParticipacionesAlumnoForProfesorUsecase listParticipaciones;

    @GET
    public List<ProyectoParticipacionResponse> list(@PathParam("alumnoUid") String alumnoUid)
            throws ForbiddenTfgException
    {
        return listParticipaciones.execute(alumnoUid);
    }
}
