package es.upsa.dasi.tfg.tareas.application.usecases;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

public interface RemoveTareaByIdUsecase
{
    void execute(long id) throws NotFoundTfgException;
}
