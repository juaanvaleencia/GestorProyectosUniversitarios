package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroResponse
{
    Long id;
    Long proyectoId;
    String usuarioUid;
    String email;
    String nombre;
    String rol;
    String rolEtiqueta;
}
