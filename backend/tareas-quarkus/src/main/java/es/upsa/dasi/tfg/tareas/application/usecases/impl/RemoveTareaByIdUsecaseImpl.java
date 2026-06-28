package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.TareaRulesService;
import es.upsa.dasi.tfg.tareas.application.usecases.RemoveTareaByIdUsecase;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveTareaByIdUsecaseImpl implements RemoveTareaByIdUsecase
{
    Repository repository;
    TareaAuthorizationService authz;
    TareaRulesService rules;

    @Inject
    public RemoveTareaByIdUsecaseImpl(Repository repository, TareaAuthorizationService authz, TareaRulesService rules)
    {
        this.repository = repository;
        this.authz = authz;
        this.rules = rules;
    }

    @Override
    public void execute(long id) throws NotFoundTfgException
    {
        Tarea tarea = repository.findById(id)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        long proyectoId = tarea.getProyectoId();
        authz.requireProductOwner(proyectoId);
        rules.validateCanDelete(tarea);
        repository.deleteById(id);
    }
}
