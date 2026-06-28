package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.usecases.ListTareasByProyectoUsecase;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListTareasByProyectoUsecaseImpl implements ListTareasByProyectoUsecase
{
    @Inject Repository repository;
    @Inject TareaAuthorizationService authz;

    @Override
    public List<Tarea> execute(long proyectoId) {
        authz.requireViewAccess(proyectoId);
        return repository.findByProyectoId(proyectoId);
    }
}

