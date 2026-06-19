package es.upsa.dasi.tfg.proyectos.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProyectoPutRequest
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
