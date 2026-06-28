package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoGrupoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
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
    Repository repository;

    @Inject
    public ProfesorGruposResource(Repository repository)
    {
        this.repository = repository;
    }

    @GET
    public List<ProyectoGrupoSupervisionResponse> list(@PathParam("plantillaId") long plantillaId)
            throws TfgException
    {
        return repository.findGruposByPlantilla(plantillaId);
    }
}
