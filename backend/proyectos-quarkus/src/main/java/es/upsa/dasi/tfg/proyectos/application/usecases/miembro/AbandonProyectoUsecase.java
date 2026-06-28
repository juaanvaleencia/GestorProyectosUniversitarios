package es.upsa.dasi.tfg.proyectos.application.usecases.miembro;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;

public interface AbandonProyectoUsecase
{
    void execute(long proyectoId) throws NotFoundTfgException;
}
