package es.upsa.dasi.tfg.common.domain.exceptions;

public class TfgException extends Exception
{
    public TfgException(String message) {
        super(message);
    }

    public TfgException(String message, Throwable cause) {
        super(message, cause);
    }
}
