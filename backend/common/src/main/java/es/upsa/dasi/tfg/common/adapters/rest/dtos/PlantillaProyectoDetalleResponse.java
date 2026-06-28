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
public class PlantillaProyectoDetalleResponse
{
    Long id;
    Long asignaturaId;
    String asignaturaNombre;
    String titulo;
    String descripcion;
    Integer orden;
    String fechaInicioSugerida;
    String fechaFinSugerida;
    String tutorNombre;
    List<PlantillaTareaResponse> tareas;
    List<PlantillaHitoResponse> hitos;
}
