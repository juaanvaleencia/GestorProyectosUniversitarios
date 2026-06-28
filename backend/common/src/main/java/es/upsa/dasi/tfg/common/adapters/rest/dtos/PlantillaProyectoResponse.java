package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantillaProyectoResponse
{
    Long id;
    Long asignaturaId;
    String titulo;
    String descripcion;
    Integer orden;
    String fechaInicioSugerida;
    String fechaFinSugerida;
    String tutorNombre;
    int numTareas;
    int numHitos;
}
