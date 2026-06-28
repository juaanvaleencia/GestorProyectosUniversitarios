package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UniversidadRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    @Mapping(target = "tipo", expression = "java(es.upsa.dasi.tfg.common.domain.model.TipoUsuario.fromDb(row.getTipo()))")
    Usuario toUsuario(UsuarioRow row);

    @Mapping(target = "tipo", expression = "java(usuario.getTipo() != null ? usuario.getTipo().name() : \"ESTUDIANTE\")")
    UsuarioRow toUsuarioRow(Usuario usuario);

    Notificacion toNotificacion(NotificacionRow row);
    Universidad toUniversidad(UniversidadRow row);
}
