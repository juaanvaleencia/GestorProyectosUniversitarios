package es.upsa.dasi.tfg.usuarios.application.usecases.mappers;

import es.upsa.dasi.tfg.usuarios.domain.model.SyncUsuarioCommand;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface UsecaseMapper
{
    Usuario toUsuario(SyncUsuarioCommand command);
}
