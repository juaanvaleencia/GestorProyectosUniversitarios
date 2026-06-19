package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.usecases.RemoveTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveTareaByIdUsecaseImpl implements RemoveTareaByIdUsecase
{
    Repository repository;
    TareaAuthorizationService authz;

    @Inject
    public RemoveTareaByIdUsecaseImpl(Repository repository, TareaAuthorizationService authz)
    {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public void execute(long id) throws NotFoundTfgException
    {
        long proyectoId = authz.proyectoIdOfTarea(id);
        authz.requireMember(proyectoId);
        repository.deleteById(id);
    }
}
