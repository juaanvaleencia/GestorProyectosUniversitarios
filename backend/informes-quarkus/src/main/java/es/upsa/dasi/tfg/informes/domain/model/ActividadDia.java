package es.upsa.dasi.tfg.informes.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActividadDia
{
    String dia;
    int tareas;
}
