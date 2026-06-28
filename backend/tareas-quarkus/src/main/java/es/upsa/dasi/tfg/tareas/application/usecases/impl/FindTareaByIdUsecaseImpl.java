package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.usecases.FindTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class FindTareaByIdUsecaseImpl implements FindTareaByIdUsecase
{
    Repository repository;
    TareaAuthorizationService authz;

    @Inject
    public FindTareaByIdUsecaseImpl(Repository repository, TareaAuthorizationService authz)
    {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public Optional<Tarea> execute(long id)
    {
        Optional<Tarea> tarea = repository.findById(id);
        tarea.ifPresent(t -> authz.requireViewAccess(t.getProyectoId()));
        return tarea;
    }
}
