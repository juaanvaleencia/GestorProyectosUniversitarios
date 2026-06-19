package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

public interface RemoveProyectoByIdUsecase
{
    void execute(long id) throws NotFoundTfgException;
}
