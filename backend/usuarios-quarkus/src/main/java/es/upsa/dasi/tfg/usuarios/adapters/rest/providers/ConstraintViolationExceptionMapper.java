package es.upsa.dasi.tfg.usuarios.adapters.rest.providers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
    @Override
    public Response toResponse(ConstraintViolationException exception)
    {
        List<ErrorResponse> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String message = violation.getMessage();
            String path = violation.getPropertyPath().toString();
            errors.add(new ErrorResponse(path, message));
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errors)
                .build();
    }
}
