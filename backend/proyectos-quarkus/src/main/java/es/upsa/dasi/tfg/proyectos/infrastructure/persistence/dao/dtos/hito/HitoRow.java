package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.hito;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;

@Data
@Builder
@With
public class HitoRow
{
    Long id;
    Long proyectoId;
    String titulo;
    LocalDate fecha;
    boolean completado;
}
