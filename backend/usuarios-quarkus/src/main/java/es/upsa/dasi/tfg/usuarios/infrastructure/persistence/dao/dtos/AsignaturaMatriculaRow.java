package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsignaturaMatriculaRow
{
    Long id;
    Long universidadId;
    String nombre;
    String descripcion;
    String tutorNombre;
}
