package es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.dtos.TareaRow;

import java.util.List;
import java.util.Optional;

public interface Dao
{
    List<TareaRow> selectTareasByProyecto(long proyectoId);
    Optional<TareaRow> selectTareaById(long id);
    TareaRow insertTarea(TareaRow row);
    Optional<TareaRow> updateTarea(TareaRow row);
    int deleteTareaById(long id);
}
