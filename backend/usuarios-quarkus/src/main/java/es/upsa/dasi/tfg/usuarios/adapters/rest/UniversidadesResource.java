package es.upsa.dasi.tfg.usuarios.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.UniversidadResponse;
import es.upsa.dasi.tfg.usuarios.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListUniversidadesUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Universidades", description = "Catálogo de universidades")
@Path("/api/universidades")
@Produces(MediaType.APPLICATION_JSON)
public class UniversidadesResource
{
    ListUniversidadesUsecase listUniversidadesUsecase;
    ResponseMappers responseMappers;

    @Inject
    public UniversidadesResource(ListUniversidadesUsecase listUniversidadesUsecase, ResponseMappers responseMappers)
    {
        this.listUniversidadesUsecase = listUniversidadesUsecase;
        this.responseMappers = responseMappers;
    }

    @GET
    public List<UniversidadResponse> list()
    {
        return listUniversidadesUsecase.execute().stream()
                .map(responseMappers::toUniversidadResponse)
                .toList();
    }
}
