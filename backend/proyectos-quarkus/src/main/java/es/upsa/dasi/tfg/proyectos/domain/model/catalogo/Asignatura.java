package es.upsa.dasi.tfg.proyectos.domain.model.catalogo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Asignatura
{
    private Long id;
    private Long universidadId;
    private String nombre;
    private String descripcion;
    private String tutorNombre;
}
