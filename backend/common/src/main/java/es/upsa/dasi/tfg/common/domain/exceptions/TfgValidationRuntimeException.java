package es.upsa.dasi.tfg.common.domain.exceptions;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;

public class TfgValidationRuntimeException extends TfgRuntimeException
{
    private final ErrorResponse[] errors;

    public TfgValidationRuntimeException(ErrorResponse[] errors)
    {
        super("Error de validación");
        this.errors = errors;
    }

    public ErrorResponse[] getErrors()
    {
        return errors;
    }
}
