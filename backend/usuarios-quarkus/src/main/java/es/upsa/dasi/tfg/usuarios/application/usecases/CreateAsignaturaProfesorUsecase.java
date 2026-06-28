package es.upsa.dasi.tfg.usuarios.application.usecases;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreateAsignaturaProfesorRequest;

public interface CreateAsignaturaProfesorUsecase
{
    AsignaturaResponse execute(CreateAsignaturaProfesorRequest request);
}
