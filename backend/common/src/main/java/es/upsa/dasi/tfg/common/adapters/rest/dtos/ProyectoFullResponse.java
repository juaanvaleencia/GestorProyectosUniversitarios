package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.json.bind.annotation.JsonbTransient;
import lombok.*;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class ProyectoFullResponse
{
    private Long id;
    private String titulo;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private String propietarioUid;
    private String creadoEn;
    private String actualizadoEn;
    @JsonbTransient
    private URI uri;
}
