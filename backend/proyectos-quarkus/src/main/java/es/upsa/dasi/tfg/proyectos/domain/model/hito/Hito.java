package es.upsa.dasi.tfg.proyectos.domain.model.hito;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Hito
{
    Long id;
    Long proyectoId;
    String titulo;
    LocalDate fecha;
    boolean completado;
}
