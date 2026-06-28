package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.TareaRulesService;
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
    TareaRulesService rules;
    UsecaseMapper usecaseMapper;

    @Inject
    public AddTareaUsecaseImpl(
            Repository repository,
            TareaAuthorizationService authz,
            TareaRulesService rules,
            UsecaseMapper usecaseMapper)
    {
        this.repository = repository;
        this.authz = authz;
        this.rules = rules;
        this.usecaseMapper = usecaseMapper;
    }

    @Override
    public Tarea execute(long proyectoId, AddTareaCommand command)
    {
        authz.requireProductOwner(proyectoId);
        rules.validateResponsable(proyectoId, command.getResponsableUid());
        rules.validateEstadoConResponsable(command.getEstado(), command.getResponsableUid());

        Tarea tarea = usecaseMapper.toTarea(command, proyectoId);
        if (command.getTareaPadreId() != null) {
            Tarea padre = rules.validateSubtareaParent(proyectoId, command.getTareaPadreId());
            int existentes = repository.countSubtareasByPadre(padre.getId());
            String letra = rules.siguienteLetraSubtarea(padre.getId());
            String responsableSubtarea = existentes == 0 ? padre.getResponsableUid() : null;
            tarea = tarea.toBuilder()
                    .tareaPadreId(padre.getId())
                    .letraSubtarea(letra)
                    .origen("ALUMNO")
                    .estado("PENDIENTE")
                    .orden(padre.getOrden())
                    .fechaLimite(padre.getFechaLimite())
                    .responsableUid(responsableSubtarea)
                    .build();
        }

        Tarea creada = repository.add(tarea);
        String responsableUid = creada.getResponsableUid();
        if (responsableUid != null && !responsableUid.isBlank()) {
            repository.addNotificacion(
                    responsableUid,
                    "Se te ha asignado la tarea «" + etiquetaTarea(creada) + "».");
        }
        return creada;
    }

    private static String etiquetaTarea(Tarea tarea) {
        if (tarea.getLetraSubtarea() != null && !tarea.getLetraSubtarea().isBlank()) {
            return tarea.getLetraSubtarea() + " · " + tarea.getTitulo();
        }
        return tarea.getTitulo();
    }
}
