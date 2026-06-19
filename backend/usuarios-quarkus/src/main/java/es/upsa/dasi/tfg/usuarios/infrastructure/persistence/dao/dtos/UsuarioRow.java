package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class UsuarioRow
{
    String firebaseUid;
    String email;
    String nombre;
    String avatarUrl;
}
