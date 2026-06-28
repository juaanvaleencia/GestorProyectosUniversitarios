package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActividadDiaRow
{
    String dia;
    String fecha;
    int tareas;
}
