package es.upsa.dasi.tfg.usuarios.adapters.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioSyncRequest
{
    @NotBlank
    private String firebaseUid;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String nombre;
    private String avatarUrl;
    private Long universidadId;
}
