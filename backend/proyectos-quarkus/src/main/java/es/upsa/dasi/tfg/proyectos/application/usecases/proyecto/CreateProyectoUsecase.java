package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto;

import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.AddProyectoCommand;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

public interface CreateProyectoUsecase
{
    Proyecto execute(AddProyectoCommand command);
}
