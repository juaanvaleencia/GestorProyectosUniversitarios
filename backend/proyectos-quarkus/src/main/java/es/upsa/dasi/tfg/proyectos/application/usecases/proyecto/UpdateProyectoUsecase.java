package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ReplaceProyectoCommand;

public interface UpdateProyectoUsecase
{
    void execute(long id, ReplaceProyectoCommand command) throws NotFoundTfgException;
}
