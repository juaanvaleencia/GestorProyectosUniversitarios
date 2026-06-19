package es.upsa.dasi.tfg.tareas.application.usecases;

import es.upsa.dasi.tfg.tareas.domain.model.AddTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;

public interface AddTareaUsecase
{
    Tarea execute(long proyectoId, AddTareaCommand command);
}
