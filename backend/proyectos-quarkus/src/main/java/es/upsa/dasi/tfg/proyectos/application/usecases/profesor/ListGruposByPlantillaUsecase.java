package es.upsa.dasi.tfg.proyectos.application.usecases.profesor;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoGrupoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

import java.util.List;

public interface ListGruposByPlantillaUsecase
{
    List<ProyectoGrupoSupervisionResponse> execute(long plantillaId) throws NotFoundTfgException;
}
