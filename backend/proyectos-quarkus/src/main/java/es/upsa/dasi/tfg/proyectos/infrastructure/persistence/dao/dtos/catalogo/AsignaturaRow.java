package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AsignaturaRow
{
    Long id;
    Long universidadId;
    String nombre;
    String descripcion;
    String tutorNombre;
}
