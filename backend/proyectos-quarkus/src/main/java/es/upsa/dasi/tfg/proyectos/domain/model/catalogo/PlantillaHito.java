package es.upsa.dasi.tfg.proyectos.domain.model.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantillaHito
{
    private Long id;
    private String titulo;
    private LocalDate fechaSugerida;
    private Integer orden;
}
