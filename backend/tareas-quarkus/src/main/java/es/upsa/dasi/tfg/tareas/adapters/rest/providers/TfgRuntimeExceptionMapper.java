package es.upsa.dasi.tfg.tareas.adapters.rest.providers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TfgRuntimeExceptionMapper implements ExceptionMapper<TfgRuntimeException>
{
    @Override
    public Response toResponse(TfgRuntimeException exception)
    {
        if (exception instanceof TfgValidationRuntimeException validationRuntimeException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(validationRuntimeException.getErrors())
                    .build();
        }
        if (exception instanceof ForbiddenTfgException forbiddenTfgException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(ErrorResponse.builder()
                            .status("403")
                            .message(forbiddenTfgException.getMessage())
                            .build())
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.builder()
                        .status("500")
                        .message(exception.getMessage())
                        .build())
                .build();
    }
}
