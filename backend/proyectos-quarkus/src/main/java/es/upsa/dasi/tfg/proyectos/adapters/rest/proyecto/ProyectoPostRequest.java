package es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProyectoPostRequest
{
    @NotBlank @Size(max = 200)
    private String titulo;
    @Size(max = 2000)
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    @NotBlank
    private String estado;
}
