package es.upsa.dasi.tfg.proyectos.application.usecases.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoParticipacionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;

import java.util.List;

public interface ListParticipacionesAlumnoForProfesorUsecase
{
    List<ProyectoParticipacionResponse> execute(String alumnoUid) throws ForbiddenTfgException;
}
