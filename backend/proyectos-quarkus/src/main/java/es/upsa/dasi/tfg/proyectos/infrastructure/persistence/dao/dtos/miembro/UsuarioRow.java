package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioRow
{
    String firebaseUid;
    String email;
    String nombre;
}
