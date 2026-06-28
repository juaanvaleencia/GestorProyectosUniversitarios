package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantillaHitoRow
{
    Long id;
    String titulo;
    LocalDate fechaSugerida;
    Integer orden;
}
