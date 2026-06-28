package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InformeHitoPendienteRow
{
    Long id;
    String titulo;
    String fecha;
    Long proyectoId;
    String proyectoTitulo;
}
