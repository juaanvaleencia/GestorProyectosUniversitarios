package es.upsa.dasi.tfg.usuarios.application.usecases.profesor.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoPerfilSupervisionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.profesor.GetAlumnoPerfilSupervisionUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetAlumnoPerfilSupervisionUsecaseImpl implements GetAlumnoPerfilSupervisionUsecase
{
    @Inject UsuarioAuthenticationService auth;
    @Inject Repository repository;

    @Override
    public AlumnoPerfilSupervisionResponse execute(String alumnoUid)
            throws ForbiddenTfgException, NotFoundTfgException
    {
        var profesor = auth.resolveCurrentUsuario();
        if (profesor.getTipo() != TipoUsuario.PROFESOR) {
            throw new ForbiddenTfgException("Solo los profesores pueden consultar perfiles de alumnos");
        }
        if (!repository.profesorSupervisaAlumno(profesor.getFirebaseUid(), alumnoUid)) {
            throw new ForbiddenTfgException("No tienes acceso al perfil de este alumno");
        }

        var alumno = repository.findByUid(alumnoUid)
                .orElseThrow(() -> new NotFoundTfgException("Alumno no encontrado"));

        List<AsignaturaResponse> asignaturas = repository
                .findAsignaturasMatriculadasByUsuario(alumnoUid).stream()
                .map(row -> AsignaturaResponse.builder()
                        .id(row.getId())
                        .universidadId(row.getUniversidadId())
                        .nombre(row.getNombre())
                        .descripcion(row.getDescripcion())
                        .tutorNombre(row.getTutorNombre())
                        .build())
                .toList();

        String universidadNombre = null;
        if (alumno.getUniversidadId() != null) {
            universidadNombre = repository.findUniversidadById(alumno.getUniversidadId())
                    .map(u -> u.getNombre())
                    .orElse(null);
        }

        return AlumnoPerfilSupervisionResponse.builder()
                .uid(alumno.getFirebaseUid())
                .nombre(alumno.getNombre())
                .email(alumno.getEmail())
                .avatarUrl(alumno.getAvatarUrl())
                .universidadNombre(universidadNombre)
                .asignaturasMatriculadas(asignaturas)
                .participaciones(List.of())
                .build();
    }
}
