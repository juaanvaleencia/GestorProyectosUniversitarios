package es.upsa.dasi.tfg.informes.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InformeHitoPendiente
{
    Long id;
    String titulo;
    String fecha;
    Long proyectoId;
    String proyectoTitulo;
}
