package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AlumnoMatriculaRow
{
    String firebaseUid;
    String nombre;
    String email;
    long asignaturaId;
    String asignaturaNombre;
    long universidadId;
    String asignaturaDescripcion;
    String tutorNombre;
}
