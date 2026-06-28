package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreateAsignaturaProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.CreateAsignaturaProfesorUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateAsignaturaProfesorUsecaseImpl implements CreateAsignaturaProfesorUsecase
{
    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;

    @Override
    @Transactional
    public AsignaturaResponse execute(CreateAsignaturaProfesorRequest request)
    {
        Usuario usuario = auth.resolveCurrentUsuario();
        if (!"PROFESOR".equals(usuario.getTipo())) {
            throw new ForbiddenTfgException("Solo los profesores pueden crear asignaturas");
        }
        if (usuario.getUniversidadId() == null) {
            throw validation("Completa tu universidad en el perfil antes de crear una asignatura");
        }

        String nombre = request.getNombre() == null ? "" : request.getNombre().trim();
        if (nombre.isBlank()) {
            throw validation("El nombre de la asignatura es obligatorio");
        }

        String descripcion = request.getDescripcion() == null || request.getDescripcion().isBlank()
                ? null
                : request.getDescripcion().trim();

        AsignaturaMatriculaRow creada = repository.createAsignaturaParaProfesor(
                usuario.getFirebaseUid(),
                usuario.getUniversidadId(),
                nombre,
                descripcion,
                usuario.getNombre());

        return AsignaturaResponse.builder()
                .id(creada.getId())
                .universidadId(creada.getUniversidadId())
                .nombre(creada.getNombre())
                .descripcion(creada.getDescripcion())
                .tutorNombre(creada.getTutorNombre())
                .build();
    }

    private static TfgValidationRuntimeException validation(String message)
    {
        return new TfgValidationRuntimeException(new ErrorResponse[] {
                ErrorResponse.builder().status("400").message(message).build()
        });
    }
}
