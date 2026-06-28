package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class GrupoSupervisionMemberRow
{
    Long proyectoId;
    String proyectoTitulo;
    String proyectoEstado;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    LocalDateTime actualizadoEn;
    String propietarioUid;
    String miembroUid;
    String miembroNombre;
    String miembroEmail;
    String miembroRol;
}
