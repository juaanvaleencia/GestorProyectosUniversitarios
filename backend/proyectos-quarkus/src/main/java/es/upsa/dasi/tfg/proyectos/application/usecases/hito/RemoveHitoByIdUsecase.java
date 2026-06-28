package es.upsa.dasi.tfg.proyectos.application.usecases.hito;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

public interface RemoveHitoByIdUsecase
{
    void execute(long proyectoId, long hitoId) throws NotFoundTfgException;
}
