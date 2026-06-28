package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlantillaProyectoRequest
{
    @NotBlank
    private String titulo;
    private String descripcion;
    private Integer orden;
    private String fechaInicioSugerida;
    private String fechaFinSugerida;
    @Valid
    @Size(max = 50)
    private List<PlantillaTareaInput> tareas;
    @Valid
    @Size(max = 50)
    private List<PlantillaHitoInput> hitos;
}
