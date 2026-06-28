package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Perfil del usuario autenticado, matrículas y participaciones")
public class UsuarioPerfilResponse
{
    String nombre;
    String email;
    String avatarUrl;
    Long universidadId;
    String universidadNombre;
    String tipo;
    boolean matriculacionCompleta;
    List<AsignaturaResponse> asignaturasMatriculadas;
    List<AsignaturaResponse> asignaturasImpartidas;
    List<ProyectoParticipacionResponse> participaciones;
}
