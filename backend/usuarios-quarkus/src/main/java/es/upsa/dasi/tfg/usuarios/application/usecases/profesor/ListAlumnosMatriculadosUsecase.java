package es.upsa.dasi.tfg.usuarios.application.usecases.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AlumnoMatriculadoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;

import java.util.List;

public interface ListAlumnosMatriculadosUsecase
{
    List<AlumnoMatriculadoResponse> execute() throws ForbiddenTfgException;
}
