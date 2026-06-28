package es.upsa.dasi.tfg.informes.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.InformesResumenResponse;
import es.upsa.dasi.tfg.informes.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.informes.application.InformesAuthenticationService;
import es.upsa.dasi.tfg.informes.application.usecases.ListInformesResumenUsecase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Informes", description = "Resúmenes y estadísticas de actividad")
@Path("/api/informes")
@Produces(MediaType.APPLICATION_JSON)
public class InformesResource
{
    ListInformesResumenUsecase listInformesResumen;
    InformesAuthenticationService auth;
    ResponseMappers responseMappers;

    @Inject
    public InformesResource(
            ListInformesResumenUsecase listInformesResumen,
            InformesAuthenticationService auth,
            ResponseMappers responseMappers)
    {
        this.listInformesResumen = listInformesResumen;
        this.auth = auth;
        this.responseMappers = responseMappers;
    }

    @GET
    @Path("resumen")
    public InformesResumenResponse resumen()
    {
        return responseMappers.toInformesResumenResponse(listInformesResumen.execute(auth.getCurrentUid()));
    }
}
