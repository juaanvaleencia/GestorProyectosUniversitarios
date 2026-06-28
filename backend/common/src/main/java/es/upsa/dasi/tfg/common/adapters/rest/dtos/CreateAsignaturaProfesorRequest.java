package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAsignaturaProfesorRequest
{
    @NotBlank(message = "El nombre de la asignatura es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    String nombre;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    String descripcion;
}
