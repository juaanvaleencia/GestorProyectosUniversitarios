package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;

import java.util.List;
import java.util.Optional;

public interface Dao
{
    Optional<UsuarioRow> selectUsuarioByUid(String firebaseUid);
    UsuarioRow insertUsuario(UsuarioRow row);
    List<NotificacionRow> selectNotificacionesByUsuario(String usuarioUid);
}
