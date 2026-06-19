package es.upsa.dasi.tfg.common.domain.exceptions;

public class TfgRuntimeException extends RuntimeException
{
    public TfgRuntimeException(Throwable cause) {
        super(cause);
    }

    public TfgRuntimeException(String message) {
        super(message);
    }
}
