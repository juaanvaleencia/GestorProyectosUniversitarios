package es.upsa.dasi.tfg.informes.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InformeTareaPendiente
{
    Long id;
    String titulo;
    String estado;
    String fechaLimite;
    Long proyectoId;
    String proyectoTitulo;
}
