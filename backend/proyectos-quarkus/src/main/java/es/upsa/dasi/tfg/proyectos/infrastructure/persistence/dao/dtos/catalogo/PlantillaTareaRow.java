package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantillaTareaRow
{
    Long id;
    String titulo;
    String descripcion;
    Integer orden;
    LocalDate fechaLimiteSugerida;
}
