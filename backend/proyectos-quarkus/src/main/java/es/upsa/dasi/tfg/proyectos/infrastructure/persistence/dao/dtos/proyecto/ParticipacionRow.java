package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class ParticipacionRow
{
    Long proyectoId;
    String titulo;
    String estado;
    String rol;
}
