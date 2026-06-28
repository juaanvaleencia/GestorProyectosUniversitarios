package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Tarea de un proyecto, con estado, prioridad y responsable")
public class TareaResponse
{
    Long id;
    Long proyectoId;
    String titulo;
    String descripcion;
    String estado;
    String prioridad;
    String responsableUid;
    String fechaLimite;
    int orden;
    String origen;
    Long tareaPadreId;
    String letraSubtarea;
}
