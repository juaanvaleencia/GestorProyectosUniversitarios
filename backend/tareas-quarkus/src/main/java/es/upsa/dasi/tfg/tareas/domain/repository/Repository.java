package es.upsa.dasi.tfg.tareas.domain.repository;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    List<Tarea> findByProyectoId(long proyectoId);
    Optional<Tarea> findById(long id);
    Tarea add(Tarea tarea);
    Tarea update(Tarea tarea) throws NotFoundTfgException;
    void deleteById(long id) throws NotFoundTfgException;
}
