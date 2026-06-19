package es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.mappers.DaoMappers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositoryImpl implements Repository
{
    private final Dao dao;
    private final DaoMappers mappers;

    @Inject
    public RepositoryImpl(Dao dao, DaoMappers mappers)
    {
        this.dao = dao;
        this.mappers = mappers;
    }

    @Override
    public List<Tarea> findByProyectoId(long proyectoId)
    {
        return dao.selectTareasByProyecto(proyectoId).stream().map(mappers::toTarea).toList();
    }

    @Override
    public Optional<Tarea> findById(long id)
    {
        return dao.selectTareaById(id).map(mappers::toTarea);
    }

    @Override
    public Tarea add(Tarea tarea)
    {
        return mappers.toTarea(dao.insertTarea(mappers.toTareaRow(tarea)));
    }

    @Override
    public Tarea update(Tarea tarea) throws NotFoundTfgException
    {
        return dao.updateTarea(mappers.toTareaRow(tarea))
                .map(mappers::toTarea)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + tarea.getId()));
    }

    @Override
    public void deleteById(long id) throws NotFoundTfgException
    {
        if (dao.deleteTareaById(id) == 0) {
            throw new NotFoundTfgException("Tarea no encontrada: " + id);
        }
    }
}
