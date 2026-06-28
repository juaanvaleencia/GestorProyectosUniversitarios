package es.upsa.dasi.tfg.usuarios.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncUsuarioCommand
{
    private String firebaseUid;
    private String email;
    private String nombre;
    private String avatarUrl;
    private Long universidadId;
}
