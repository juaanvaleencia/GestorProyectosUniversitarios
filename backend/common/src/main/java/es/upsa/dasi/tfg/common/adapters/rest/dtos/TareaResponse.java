package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
