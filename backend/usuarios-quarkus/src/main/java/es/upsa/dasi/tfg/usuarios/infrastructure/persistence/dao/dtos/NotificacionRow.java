package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDateTime;

@Data
@Builder
@With
public class NotificacionRow
{
    Long id;
    String usuarioUid;
    String texto;
    boolean leida;
    LocalDateTime creadoEn;
    String tipo;
    Long invitacionId;
    Long proyectoId;
    String invitacionEstado;
    String invitacionSituacion;
}
