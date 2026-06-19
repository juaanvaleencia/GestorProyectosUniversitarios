package es.upsa.dasi.tfg.tareas.application.usecases;

import es.upsa.dasi.tfg.tareas.domain.model.Tarea;

import java.util.List;

public interface ListTareasByProyectoUsecase
{
    List<Tarea> execute(long proyectoId);
}

