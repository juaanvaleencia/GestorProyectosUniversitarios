package es.upsa.dasi.tfg.proyectos.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Miembro
{
    Long id;
    Long proyectoId;
    String usuarioUid;
    String email;
    String nombre;
    String rol;
}
