package es.upsa.dasi.tfg.proyectos.domain.model.miembro;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsuarioRegistrado
{
    String firebaseUid;
    String email;
    String nombre;
}
