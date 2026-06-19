package es.upsa.dasi.tfg.common.domain.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class Usuario
{
    private String firebaseUid;
    private String email;
    private String nombre;
    private String avatarUrl;
}
