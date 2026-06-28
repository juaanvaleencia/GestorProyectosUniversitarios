package es.upsa.dasi.tfg.proyectos.adapters.rest.hito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HitoPostRequest
{
    @NotBlank
    private String titulo;
    @NotNull
    private String fecha;
    private boolean completado;
}
