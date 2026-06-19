package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.SyncUsuarioUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.mappers.UsecaseMapper;
import es.upsa.dasi.tfg.usuarios.domain.model.SyncUsuarioCommand;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SyncUsuarioUsecaseImpl implements SyncUsuarioUsecase
{
    Repository repository;
    UsecaseMapper usecaseMapper;
    UsuarioAuthenticationService auth;

    @Inject
    public SyncUsuarioUsecaseImpl(Repository repository, UsecaseMapper usecaseMapper, UsuarioAuthenticationService auth)
    {
        this.repository = repository;
        this.usecaseMapper = usecaseMapper;
        this.auth = auth;
    }

    @Override
    public Usuario execute(SyncUsuarioCommand command)
    {
        if (!auth.getCurrentUid().equals(command.getFirebaseUid())) {
            throw new ForbiddenTfgException("El UID del token no coincide con el usuario a sincronizar");
        }
        return repository.add(usecaseMapper.toUsuario(command));
    }
}
