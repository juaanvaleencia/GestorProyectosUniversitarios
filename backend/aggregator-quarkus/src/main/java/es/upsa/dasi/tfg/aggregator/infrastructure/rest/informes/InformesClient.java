package es.upsa.dasi.tfg.aggregator.infrastructure.rest.informes;

import es.upsa.dasi.tfg.aggregator.infrastructure.rest.ForwardAuthorizationFactory;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers.TfgResponseExceptionMapper;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.InformesResumenResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "informes-api")
@RegisterClientHeaders(ForwardAuthorizationFactory.class)
@RegisterProvider(TfgResponseExceptionMapper.class)
@Path("/api/informes")
public interface InformesClient
{
    @GET
    @Path("resumen")
    @Produces(MediaType.APPLICATION_JSON)
    InformesResumenResponse resumen() throws TfgException;
}
