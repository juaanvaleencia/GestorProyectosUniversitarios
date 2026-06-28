package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.UpdateMatriculasUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class UpdateMatriculasUsecaseImpl implements UpdateMatriculasUsecase
{
    private static final int MAX_MATRICULAS = 10;

    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;

    @Override
    public List<AsignaturaResponse> execute(List<Long> asignaturaIds)
    {
        Usuario usuario = auth.resolveCurrentUsuario();
        if (usuario.getUniversidadId() == null) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("Indica tu universidad antes de matricularte").build()
            });
        }
        if (asignaturaIds == null || asignaturaIds.isEmpty()) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("Selecciona al menos una asignatura").build()
            });
        }
        if (asignaturaIds.size() > MAX_MATRICULAS) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("Puedes seleccionar un máximo de 10 asignaturas").build()
            });
        }
        if (new HashSet<>(asignaturaIds).size() != asignaturaIds.size()) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("No repitas asignaturas en la selección").build()
            });
        }
        if (!repository.asignaturasPertenecenAUniversidad(asignaturaIds, usuario.getUniversidadId())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("Todas las asignaturas deben pertenecer a tu universidad")
                            .build()
            });
        }

        String uid = auth.getCurrentUid();
        if (usuario.getTipo() == TipoUsuario.PROFESOR) {
            var ocupadas = repository.findNombresAsignaturasOcupadasPorOtrosProfesores(asignaturaIds, uid);
            if (!ocupadas.isEmpty()) {
                throw new TfgValidationRuntimeException(new ErrorResponse[] {
                        ErrorResponse.builder()
                                .status("400")
                                .message("Estas asignaturas ya están asignadas a otro profesor: "
                                        + String.join(", ", ocupadas))
                                .build()
                });
            }
            var previas = repository.findAsignaturasImpartidasByProfesor(uid).stream()
                    .map(AsignaturaMatriculaRow::getId)
                    .toList();
            repository.replaceProfesorAsignaturas(uid, asignaturaIds);
            var nuevas = new HashSet<>(asignaturaIds);
            for (Long asignaturaId : previas) {
                if (!nuevas.contains(asignaturaId)) {
                    repository.clearAsignaturaTutor(asignaturaId);
                }
            }
            for (Long asignaturaId : asignaturaIds) {
                repository.updateAsignaturaTutor(asignaturaId, uid, usuario.getNombre());
            }
            return repository.findAsignaturasImpartidasByProfesor(uid).stream()
                    .map(this::toResponse)
                    .toList();
        }

        repository.replaceAsignaturasMatriculadas(uid, asignaturaIds);
        return repository.findAsignaturasMatriculadasByUsuario(uid).stream()
                .map(this::toResponse)
                .toList();
    }

    private AsignaturaResponse toResponse(AsignaturaMatriculaRow row)
    {
        return AsignaturaResponse.builder()
                .id(row.getId())
                .universidadId(row.getUniversidadId())
                .nombre(row.getNombre())
                .descripcion(row.getDescripcion())
                .tutorNombre(row.getTutorNombre())
                .build();
    }
}
