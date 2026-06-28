package es.upsa.dasi.tfg.proyectos.adapters.rest.miembro;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MiembroInvitePostRequest
{
    @NotBlank @Email
    private String email;
    @NotBlank
    private String rol;
}
