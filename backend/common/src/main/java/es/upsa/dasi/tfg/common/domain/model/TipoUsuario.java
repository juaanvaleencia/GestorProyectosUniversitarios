package es.upsa.dasi.tfg.common.domain.model;

public enum TipoUsuario
{
    ESTUDIANTE,
    PROFESOR;

    public static TipoUsuario fromDb(String value)
    {
        if (value == null || value.isBlank()) {
            return ESTUDIANTE;
        }
        return TipoUsuario.valueOf(value.trim().toUpperCase());
    }
}
