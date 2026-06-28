package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantillaProyectoRow
{
    Long id;
    Long asignaturaId;
    String asignaturaNombre;
    String titulo;
    String descripcion;
    Integer orden;
    LocalDate fechaInicioSugerida;
    LocalDate fechaFinSugerida;
    String tutorNombre;
    int numTareas;
    int numHitos;
}
