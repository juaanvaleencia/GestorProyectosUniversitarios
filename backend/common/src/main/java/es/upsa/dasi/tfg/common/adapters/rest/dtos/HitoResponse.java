package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitoResponse
{
    Long id;
    Long proyectoId;
    String titulo;
    String fecha;
    boolean completado;
}
