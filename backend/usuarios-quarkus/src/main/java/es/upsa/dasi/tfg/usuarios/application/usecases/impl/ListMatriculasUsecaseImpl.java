package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListMatriculasUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListMatriculasUsecaseImpl implements ListMatriculasUsecase
{
    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;

    @Override
    public List<AsignaturaResponse> execute()
    {
        Usuario usuario = auth.resolveCurrentUsuario();
        String uid = auth.getCurrentUid();
        if (usuario.getTipo() == TipoUsuario.PROFESOR) {
            return repository.findAsignaturasImpartidasByProfesor(uid).stream()
                    .map(this::toResponse)
                    .toList();
        }
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
