package es.upsa.dasi.tfg.proyectos.domain.model.miembro;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InviteMiembroCommand
{
    String email;
    String rol;
}
