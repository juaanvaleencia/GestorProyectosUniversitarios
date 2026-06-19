package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.usecases.AddTareaUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.mappers.UsecaseMapper;
import es.upsa.dasi.tfg.tareas.domain.model.AddTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AddTareaUsecaseImpl implements AddTareaUsecase
{
    Repository repository;
    TareaAuthorizationService authz;
    UsecaseMapper usecaseMapper;

    @Inject
    public AddTareaUsecaseImpl(Repository repository, TareaAuthorizationService authz, UsecaseMapper usecaseMapper)
    {
        this.repository = repository;
        this.authz = authz;
        this.usecaseMapper = usecaseMapper;
    }

    @Override
    public Tarea execute(long proyectoId, AddTareaCommand command)
    {
        authz.requireMember(proyectoId);
        return repository.add(usecaseMapper.toTarea(command, proyectoId));
    }
}
