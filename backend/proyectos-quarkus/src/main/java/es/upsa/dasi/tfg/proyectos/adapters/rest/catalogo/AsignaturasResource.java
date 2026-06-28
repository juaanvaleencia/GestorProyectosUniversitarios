package es.upsa.dasi.tfg.proyectos.adapters.rest.catalogo;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.proyectos.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.ListAsignaturasByUniversidadUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Asignaturas", description = "Asignaturas y catálogo académico")
@Path("/api/universidades/{universidadId}/asignaturas")
@Produces(MediaType.APPLICATION_JSON)
public class AsignaturasResource
{
    @Inject ListAsignaturasByUniversidadUsecase listAsignaturas;
    @Inject ResponseMappers mappers;

    @GET
    public List<AsignaturaResponse> list(@PathParam("universidadId") long universidadId)
    {
        return listAsignaturas.execute(universidadId).stream().map(mappers::toAsignaturaResponse).toList();
    }
}
