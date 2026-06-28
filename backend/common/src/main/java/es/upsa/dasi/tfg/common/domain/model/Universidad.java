package es.upsa.dasi.tfg.common.domain.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class Universidad
{
    private Long id;
    private String codigo;
    private String nombre;
}
