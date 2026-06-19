package es.upsa.dasi.tfg.usuarios.domain.repository;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    Optional<Usuario> findByUid(String firebaseUid);
    Usuario add(Usuario usuario);
    List<Notificacion> findNotificacionesByUsuario(String usuarioUid);
}
