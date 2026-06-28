package es.upsa.dasi.tfg.usuarios.application.usecases;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;

import java.util.List;

public interface UpdateMatriculasUsecase
{
    List<AsignaturaResponse> execute(List<Long> asignaturaIds);
}
