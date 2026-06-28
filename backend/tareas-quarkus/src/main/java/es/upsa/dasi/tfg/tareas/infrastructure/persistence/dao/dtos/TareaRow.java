package es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@With
public class TareaRow
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
    String origen;
    Long tareaPadreId;
    String letraSubtarea;
}
