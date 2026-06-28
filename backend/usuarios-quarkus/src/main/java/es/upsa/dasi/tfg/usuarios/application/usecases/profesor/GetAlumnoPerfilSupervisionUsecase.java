package es.upsa.dasi.tfg.usuarios.application.usecases.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoPerfilSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

public interface GetAlumnoPerfilSupervisionUsecase
{
    AlumnoPerfilSupervisionResponse execute(String alumnoUid)
            throws ForbiddenTfgException, NotFoundTfgException;
}
