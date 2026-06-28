package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformeTareaPendienteResponse
{
    Long id;
    String titulo;
    String estado;
    String fechaLimite;
    Long proyectoId;
    String proyectoTitulo;
}
