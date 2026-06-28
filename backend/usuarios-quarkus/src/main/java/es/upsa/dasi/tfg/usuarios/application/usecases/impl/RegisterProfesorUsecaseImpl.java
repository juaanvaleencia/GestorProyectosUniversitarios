package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.RegistroProfesorRequest;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.application.EmailUniversidadValidator;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.RegisterProfesorUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RegisterProfesorUsecaseImpl implements RegisterProfesorUsecase
{
    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;
    @Inject EmailUniversidadValidator emailValidator;

    @Override
    public Usuario execute(RegistroProfesorRequest request)
    {
        if (!auth.getCurrentUid().equals(request.getFirebaseUid())) {
            throw new ForbiddenTfgException("El UID del token no coincide con el usuario a registrar");
        }
        if (!repository.existsUniversidad(request.getUniversidadId())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("La universidad indicada no existe").build()
            });
        }
        emailValidator.validar(request.getEmail(), request.getUniversidadId());

        String codigoEsperado = repository.findCodigoProfesorByUniversidadId(request.getUniversidadId())
                .orElse(null);
        if (codigoEsperado == null || !codigoEsperado.equals(request.getCodigoProfesor().trim())) {
            throw new ForbiddenTfgException("Código de profesor incorrecto para esta universidad");
        }

        var existente = repository.findByUid(request.getFirebaseUid());
        if (existente.isPresent() && existente.get().getTipo() != TipoUsuario.PROFESOR) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("Este usuario ya está registrado como estudiante")
                            .build()
            });
        }

        Usuario usuario = Usuario.builder()
                .firebaseUid(request.getFirebaseUid())
                .email(request.getEmail().trim())
                .nombre(request.getNombre().trim())
                .universidadId(request.getUniversidadId())
                .tipo(TipoUsuario.PROFESOR)
                .build();
        repository.add(usuario);

        String uid = request.getFirebaseUid();
        if (repository.findNotificacionesByUsuario(uid).isEmpty()) {
            repository.addNotificacion(uid, "Bienvenido a la plataforma de gestión de proyectos universitarios.");
        }

        return repository.findByUid(uid).orElse(usuario);
    }
}
