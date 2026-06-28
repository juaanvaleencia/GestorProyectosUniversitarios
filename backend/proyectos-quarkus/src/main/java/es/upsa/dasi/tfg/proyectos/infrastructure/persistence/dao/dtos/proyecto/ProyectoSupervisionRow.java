package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProyectoSupervisionRow
{
    Long id;
    String titulo;
    String estado;
    String propietarioNombre;
    String propietarioEmail;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    LocalDateTime actualizadoEn;
}
