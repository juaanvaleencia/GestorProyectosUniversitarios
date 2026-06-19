package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.proyectos.domain.model.AddProyectoCommand;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

public interface CreateProyectoUsecase
{
    Proyecto execute(AddProyectoCommand command);
}
