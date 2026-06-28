package es.upsa.dasi.tfg.proyectos.application.usecases.hito;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.AddHitoCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;

public interface CreateHitoUsecase
{
    Hito execute(long proyectoId, AddHitoCommand command) throws NotFoundTfgException;
}
