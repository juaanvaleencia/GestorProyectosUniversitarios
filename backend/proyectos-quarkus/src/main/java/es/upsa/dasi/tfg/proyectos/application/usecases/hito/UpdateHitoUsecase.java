package es.upsa.dasi.tfg.proyectos.application.usecases.hito;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.ReplaceHitoCommand;

public interface UpdateHitoUsecase
{
    void execute(long proyectoId, long hitoId, ReplaceHitoCommand command) throws NotFoundTfgException;
}
