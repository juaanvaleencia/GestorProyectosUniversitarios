package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaHitoInput
{
    @NotBlank
    private String titulo;
    @NotBlank
    private String fechaSugerida;
    private Integer orden;
}
