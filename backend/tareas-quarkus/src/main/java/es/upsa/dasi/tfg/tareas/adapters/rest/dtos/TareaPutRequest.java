package es.upsa.dasi.tfg.tareas.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TareaPutRequest
{
    @NotBlank @Size(max = 200)
    private String titulo;
    @Size(max = 1000)
    private String descripcion;
    @NotBlank
    private String estado;
    @NotBlank
    private String prioridad;
    private String responsableUid;
    private String fechaLimite;
    private int orden;
}
