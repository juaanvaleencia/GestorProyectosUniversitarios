package es.upsa.dasi.tfg.proyectos.adapters.rest.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoGrupoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListGruposByPlantillaUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Profesor", description = "Grupos de proyecto supervisados por el profesor")
@Path("/api/profesor/plantillas/{plantillaId}/grupos")
@Produces(MediaType.APPLICATION_JSON)
public class ProfesorGruposResource
{
    @Inject ListGruposByPlantillaUsecase listGrupos;

    @GET
    public List<ProyectoGrupoSupervisionResponse> list(@PathParam("plantillaId") long plantillaId)
            throws NotFoundTfgException
    {
        return listGrupos.execute(plantillaId);
    }
}
