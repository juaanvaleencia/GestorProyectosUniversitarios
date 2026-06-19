package es.upsa.dasi.tfg.tareas.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Tarea
{
    Long id;
    Long proyectoId;
    String titulo;
    String descripcion;
    String estado;
    String prioridad;
    String responsableUid;
    LocalDate fechaLimite;
    int orden;
    LocalDateTime creadoEn;
}
