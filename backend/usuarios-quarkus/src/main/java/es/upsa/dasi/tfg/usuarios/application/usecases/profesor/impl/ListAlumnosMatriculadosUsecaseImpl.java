package es.upsa.dasi.tfg.usuarios.application.usecases.profesor.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoMatriculadoResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.profesor.ListAlumnosMatriculadosUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AlumnoMatriculaRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ListAlumnosMatriculadosUsecaseImpl implements ListAlumnosMatriculadosUsecase
{
    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;

    @Override
    public List<AlumnoMatriculadoResponse> execute() throws ForbiddenTfgException
    {
        var profesor = auth.resolveCurrentUsuario();
        if (profesor.getTipo() != TipoUsuario.PROFESOR) {
            throw new ForbiddenTfgException("Solo los profesores pueden consultar alumnos matriculados");
        }

        Map<String, AlumnoMatriculadoResponse> alumnos = new LinkedHashMap<>();
        for (AlumnoMatriculaRow row : repository.findAlumnosMatriculadosByProfesor(profesor.getFirebaseUid())) {
            AlumnoMatriculadoResponse alumno = alumnos.computeIfAbsent(row.getFirebaseUid(), uid ->
                    AlumnoMatriculadoResponse.builder()
                            .uid(uid)
                            .nombre(row.getNombre())
                            .email(row.getEmail())
                            .asignaturas(new ArrayList<>())
                            .build()
            );
            alumno.getAsignaturas().add(AsignaturaResponse.builder()
                    .id(row.getAsignaturaId())
                    .universidadId(row.getUniversidadId())
                    .nombre(row.getAsignaturaNombre())
                    .descripcion(row.getAsignaturaDescripcion())
                    .tutorNombre(row.getTutorNombre())
                    .build());
        }
        return new ArrayList<>(alumnos.values());
    }
}
