package es.upsa.dasi.tfg.tareas.application.usecases;

import es.upsa.dasi.tfg.tareas.domain.model.Tarea;

import java.util.Optional;

public interface FindTareaByIdUsecase
{
    Optional<Tarea> execute(long id);
}
