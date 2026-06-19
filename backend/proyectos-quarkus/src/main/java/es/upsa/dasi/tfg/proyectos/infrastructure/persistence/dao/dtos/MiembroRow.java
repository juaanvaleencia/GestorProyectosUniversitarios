package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class MiembroRow
{
    Long id;
    Long proyectoId;
    String usuarioUid;
    String rol;
    String email;
    String nombre;
}
