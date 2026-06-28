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
@Schema(description = "Invitación pendiente o resuelta a un proyecto")
public class InvitacionProyectoResponse
{
    Long id;
    Long proyectoId;
    String usuarioUid;
    String email;
    String nombre;
    String rol;
    String rolEtiqueta;
    String estado;
    String invitadoPorNombre;
    String creadoEn;
}
