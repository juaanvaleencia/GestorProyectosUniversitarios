package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@With
public class ProyectoRow
{
    Long id;
    String titulo;
    String descripcion;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    String estado;
    String propietarioUid;
    Long plantillaId;
    Long asignaturaId;
    String asignaturaNombre;
    LocalDateTime creadoEn;
    LocalDateTime actualizadoEn;
}
