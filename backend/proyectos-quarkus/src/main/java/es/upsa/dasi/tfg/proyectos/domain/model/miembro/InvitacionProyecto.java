package es.upsa.dasi.tfg.proyectos.domain.model.miembro;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class InvitacionProyecto
{
    Long id;
    Long proyectoId;
    String usuarioUid;
    String email;
    String nombre;
    String rol;
    String estado;
    String invitadoPorUid;
    String invitadoPorNombre;
    LocalDateTime creadoEn;
}
