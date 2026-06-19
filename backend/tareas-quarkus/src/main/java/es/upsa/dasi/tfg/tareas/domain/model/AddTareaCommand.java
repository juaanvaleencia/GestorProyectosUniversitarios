package es.upsa.dasi.tfg.tareas.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddTareaCommand
{
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    private String responsableUid;
    private String fechaLimite;
    private int orden;
}
