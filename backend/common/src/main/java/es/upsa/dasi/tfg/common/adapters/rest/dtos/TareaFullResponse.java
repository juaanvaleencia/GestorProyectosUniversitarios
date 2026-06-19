package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.json.bind.annotation.JsonbTransient;
import lombok.*;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class TareaFullResponse
{
    private Long id;
    private Long proyectoId;
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    private String responsableUid;
    private String fechaLimite;
    private int orden;
    @JsonbTransient
    private URI uri;
}
