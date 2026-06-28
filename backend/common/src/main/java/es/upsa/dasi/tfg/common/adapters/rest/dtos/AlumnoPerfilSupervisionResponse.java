package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumnoPerfilSupervisionResponse
{
    String uid;
    String nombre;
    String email;
    String avatarUrl;
    String universidadNombre;
    List<AsignaturaResponse> asignaturasMatriculadas;
    List<ProyectoParticipacionResponse> participaciones;
}
