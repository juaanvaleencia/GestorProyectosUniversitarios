package es.upsa.dasi.tfg.proyectos.domain.model.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PlantillaProyecto
{
    private Long id;
    private Long asignaturaId;
    private String asignaturaNombre;
    private String titulo;
    private String descripcion;
    private Integer orden;
    private LocalDate fechaInicioSugerida;
    private LocalDate fechaFinSugerida;
    private String tutorNombre;
    private int numTareas;
    private int numHitos;
    private List<PlantillaTarea> tareas;
    private List<PlantillaHito> hitos;
}
