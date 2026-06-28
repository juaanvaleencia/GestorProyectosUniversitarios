package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvitacionProyectoRow
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
