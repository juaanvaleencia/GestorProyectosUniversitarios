package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notificación del usuario, incluye invitaciones a proyectos")
public class NotificacionResponse
{
    Long id;
    String usuarioUid;
    String texto;
    boolean leida;
    String creadoEn;
    String tipo;
    Long invitacionId;
    Long proyectoId;
    String invitacionEstado;
    String invitacionSituacion;
}
