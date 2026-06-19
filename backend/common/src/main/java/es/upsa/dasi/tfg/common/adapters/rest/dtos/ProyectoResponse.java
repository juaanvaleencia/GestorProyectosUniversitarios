package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoResponse
{
    Long id;
    String titulo;
    String descripcion;
    String fechaInicio;
    String fechaFin;
    String estado;
    String propietarioUid;
    String creadoEn;
    String actualizadoEn;
}
