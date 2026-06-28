package es.upsa.dasi.tfg.proyectos.application.hito;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class HitoValidationService
{
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public void requireFechaWithinProyecto(Proyecto proyecto, LocalDate fecha)
    {
        LocalDate inicio = proyecto.getFechaInicio();
        LocalDate fin = proyecto.getFechaFin();
        if (inicio == null && fin == null) {
            return;
        }
        if (inicio != null && fecha.isBefore(inicio)) {
            throw validationError(proyecto, fecha);
        }
        if (fin != null && fecha.isAfter(fin)) {
            throw validationError(proyecto, fecha);
        }
    }

    private TfgValidationRuntimeException validationError(Proyecto proyecto, LocalDate fecha)
    {
        LocalDate inicio = proyecto.getFechaInicio();
        LocalDate fin = proyecto.getFechaFin();
        String message;
        if (inicio != null && fin != null) {
            message = "La fecha del hito (" + fecha.format(ISO) + ") debe estar entre "
                    + inicio.format(ISO) + " y " + fin.format(ISO) + " (fechas del proyecto).";
        }
        else if (inicio != null) {
            message = "La fecha del hito no puede ser anterior al inicio del proyecto (" + inicio.format(ISO) + ").";
        }
        else {
            message = "La fecha del hito no puede ser posterior al fin del proyecto (" + fin.format(ISO) + ").";
        }
        return new TfgValidationRuntimeException(new ErrorResponse[] {
                ErrorResponse.builder().status("400").message(message).build()
        });
    }
}
