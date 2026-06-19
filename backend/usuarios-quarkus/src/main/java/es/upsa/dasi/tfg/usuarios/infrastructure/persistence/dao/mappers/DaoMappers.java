package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    Usuario toUsuario(UsuarioRow row);
    UsuarioRow toUsuarioRow(Usuario usuario);
    Notificacion toNotificacion(NotificacionRow row);
}
