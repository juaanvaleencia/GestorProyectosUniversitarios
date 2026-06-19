package es.upsa.dasi.tfg.usuarios.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.usuarios.adapters.rest.dtos.UsuarioSyncRequest;
import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.usuarios.domain.model.SyncUsuarioCommand;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class ResponseMappers
{
    public abstract UsuarioPerfilResponse toUsuarioPerfilResponse(Usuario usuario);

    public abstract SyncUsuarioCommand toSyncUsuarioCommand(UsuarioSyncRequest request);

    @Mapping(target = "creadoEn", expression = "java(notificacion.getCreadoEn() != null ? notificacion.getCreadoEn().toString() : null)")
    public abstract NotificacionResponse toNotificacionResponse(Notificacion notificacion);
}
