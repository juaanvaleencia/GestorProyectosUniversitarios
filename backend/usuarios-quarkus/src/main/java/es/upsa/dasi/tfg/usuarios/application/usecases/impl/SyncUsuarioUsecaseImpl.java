package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.usuarios.application.EmailUniversidadValidator;
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
    EmailUniversidadValidator emailValidator;

    @Inject
    public SyncUsuarioUsecaseImpl(
            Repository repository,
            UsecaseMapper usecaseMapper,
            UsuarioAuthenticationService auth,
            EmailUniversidadValidator emailValidator)
    {
        this.repository = repository;
        this.usecaseMapper = usecaseMapper;
        this.auth = auth;
        this.emailValidator = emailValidator;
    }

    @Override
    public Usuario execute(SyncUsuarioCommand command)
    {
        if (!auth.getCurrentUid().equals(command.getFirebaseUid())) {
            throw new ForbiddenTfgException("El UID del token no coincide con el usuario a sincronizar");
        }
        if (command.getUniversidadId() != null && !repository.existsUniversidad(command.getUniversidadId())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("La universidad indicada no existe")
                            .build()
            });
        }
        emailValidator.validar(command.getEmail(), command.getUniversidadId());
        String uid = command.getFirebaseUid();
        Usuario usuario = repository.add(usecaseMapper.toUsuario(command));
        if (repository.findNotificacionesByUsuario(uid).isEmpty()) {
            repository.addNotificacion(uid, "Bienvenido a la plataforma de proyectos universitarios.");
        }
        return repository.findByUid(uid).orElse(usuario);
    }
}
