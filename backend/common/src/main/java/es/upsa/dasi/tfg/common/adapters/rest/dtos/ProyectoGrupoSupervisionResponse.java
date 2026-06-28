package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGrupoSupervisionResponse
{
    Long id;
    String titulo;
    String estado;
    String fechaInicio;
    String fechaFin;
    String actualizadoEn;
    List<ParticipanteSupervisionResponse> participantes;
}
