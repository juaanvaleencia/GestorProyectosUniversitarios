package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarMatriculasRequest
{
    @NotNull
    @Size(min = 1, max = 10)
    private List<Long> asignaturaIds;
}
