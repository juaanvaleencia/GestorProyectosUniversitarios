package es.upsa.dasi.tfg.usuarios.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Notificacion
{
    Long id;
    String usuarioUid;
    String texto;
    boolean leida;
    LocalDateTime creadoEn;
}
