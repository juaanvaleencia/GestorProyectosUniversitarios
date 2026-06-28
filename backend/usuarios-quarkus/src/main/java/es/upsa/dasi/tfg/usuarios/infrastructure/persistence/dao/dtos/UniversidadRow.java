package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UniversidadRow
{
    Long id;
    String codigo;
    String nombre;
}
