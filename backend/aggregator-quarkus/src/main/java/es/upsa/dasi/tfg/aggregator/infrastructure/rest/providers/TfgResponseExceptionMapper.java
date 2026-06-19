package es.upsa.dasi.tfg.aggregator.infrastructure.rest.providers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class TfgResponseExceptionMapper implements ResponseExceptionMapper<TfgException>
{
    @Override
    public TfgException toThrowable(Response response)
    {
        return switch (response.getStatusInfo().toEnum())
        {
            case NOT_FOUND -> new NotFoundTfgException(response.readEntity(ErrorResponse.class).message());
            case BAD_REQUEST -> throw new TfgValidationRuntimeException(response.readEntity(ErrorResponse[].class));
            default -> new TfgException(response.readEntity(ErrorResponse.class).message());
        };
    }
}
