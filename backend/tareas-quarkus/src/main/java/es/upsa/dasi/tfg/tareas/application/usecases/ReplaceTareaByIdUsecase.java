package es.upsa.dasi.tfg.tareas.application.usecases;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.domain.model.ReplaceTareaCommand;

public interface ReplaceTareaByIdUsecase
{
    void execute(long id, ReplaceTareaCommand command) throws NotFoundTfgException;
}
