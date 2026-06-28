package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaTareaInput
{
    @NotBlank
    private String titulo;
    private String descripcion;
    private Integer orden;
    private String fechaLimiteSugerida;
}
