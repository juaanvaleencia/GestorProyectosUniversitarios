package es.upsa.dasi.tfg.proyectos.domain.model.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantillaTarea
{
    private Long id;
    private String titulo;
    private String descripcion;
    private Integer orden;
    private LocalDate fechaLimiteSugerida;
}
