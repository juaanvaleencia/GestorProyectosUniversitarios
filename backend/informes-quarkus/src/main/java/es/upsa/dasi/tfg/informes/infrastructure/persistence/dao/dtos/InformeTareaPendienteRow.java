package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InformeTareaPendienteRow
{
    Long id;
    String titulo;
    String estado;
    String fechaLimite;
    Long proyectoId;
    String proyectoTitulo;
}
