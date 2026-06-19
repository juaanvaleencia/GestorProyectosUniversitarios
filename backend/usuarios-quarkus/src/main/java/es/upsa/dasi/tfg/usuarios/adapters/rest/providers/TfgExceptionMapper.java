package es.upsa.dasi.tfg.usuarios.adapters.rest.providers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TfgExceptionMapper implements ExceptionMapper<TfgException>
{
    @Override
    public Response toResponse(TfgException exception)
    {
        return switch (exception)
        {
            case NotFoundTfgException notFoundTfgException -> Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.builder()
                            .status("404")
                            .message(notFoundTfgException.getMessage())
                            .build())
                    .build();

            default -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorResponse.builder()
                            .status("500")
                            .message(exception.getMessage())
                            .build())
                    .build();
        };
    }
}
