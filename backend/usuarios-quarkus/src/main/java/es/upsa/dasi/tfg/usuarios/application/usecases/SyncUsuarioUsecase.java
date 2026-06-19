package es.upsa.dasi.tfg.usuarios.application.usecases;

import es.upsa.dasi.tfg.usuarios.domain.model.SyncUsuarioCommand;
import es.upsa.dasi.tfg.common.domain.model.Usuario;

public interface SyncUsuarioUsecase
{
    Usuario execute(SyncUsuarioCommand command);
}
