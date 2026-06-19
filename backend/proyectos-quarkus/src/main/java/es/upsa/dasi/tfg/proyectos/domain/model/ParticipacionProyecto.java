package es.upsa.dasi.tfg.proyectos.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ParticipacionProyecto
{
    Long proyectoId;
    String titulo;
    String estado;
    String rol;
}
