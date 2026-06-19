package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.usecases.ReplaceTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.application.usecases.mappers.UsecaseMapper;
import es.upsa.dasi.tfg.tareas.domain.model.ReplaceTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReplaceTareaByIdUsecaseImpl implements ReplaceTareaByIdUsecase
{
    Repository repository;
    TareaAuthorizationService authz;
    UsecaseMapper usecaseMapper;

    @Inject
    public ReplaceTareaByIdUsecaseImpl(Repository repository, TareaAuthorizationService authz, UsecaseMapper usecaseMapper)
    {
        this.repository = repository;
        this.authz = authz;
        this.usecaseMapper = usecaseMapper;
    }

    @Override
    public void execute(long id, ReplaceTareaCommand command) throws NotFoundTfgException
    {
        Tarea actual = repository.findById(id)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        authz.requireMember(actual.getProyectoId());
        Tarea actualizada = usecaseMapper.toTarea(id, actual.getProyectoId(), command, actual.getCreadoEn());
        repository.update(actualizada);
    }
}
