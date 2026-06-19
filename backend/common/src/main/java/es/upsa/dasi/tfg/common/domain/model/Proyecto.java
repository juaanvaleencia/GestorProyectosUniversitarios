package es.upsa.dasi.tfg.common.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class Proyecto
{
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private String propietarioUid;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
