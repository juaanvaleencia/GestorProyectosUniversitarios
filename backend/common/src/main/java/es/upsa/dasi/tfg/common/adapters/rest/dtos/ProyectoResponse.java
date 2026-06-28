package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Proyecto universitario con metadatos y asignatura asociada")
public class ProyectoResponse
{
    Long id;
    String titulo;
    String descripcion;
    String fechaInicio;
    String fechaFin;
    String estado;
    String propietarioUid;
    Long asignaturaId;
    String asignaturaNombre;
    String creadoEn;
    String actualizadoEn;
}
