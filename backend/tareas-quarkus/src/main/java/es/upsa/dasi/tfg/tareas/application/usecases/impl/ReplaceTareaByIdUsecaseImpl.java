package es.upsa.dasi.tfg.tareas.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.application.TareaAuthorizationService;
import es.upsa.dasi.tfg.tareas.application.TareaRulesService;
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
    TareaRulesService rules;
    UsecaseMapper usecaseMapper;

    @Inject
    public ReplaceTareaByIdUsecaseImpl(
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
    public void execute(long id, ReplaceTareaCommand command) throws NotFoundTfgException
    {
        Tarea actual = repository.findById(id)
                .orElseThrow(() -> new NotFoundTfgException("Tarea no encontrada: " + id));
        authz.requireProductOwner(actual.getProyectoId());
        Tarea actualizada = usecaseMapper.toTarea(id, actual.getProyectoId(), command, actual.getCreadoEn(), actual);
        rules.validateTareaUpdate(actual, actualizada);
        repository.update(actualizada);

        if (rules.esPadreProfesor(actual)
                && repository.countSubtareasByPadre(actual.getId()) > 0
                && !normalizarEstado(actual.getEstado()).equals(normalizarEstado(actualizada.getEstado()))) {
            repository.updateEstadoSubtareasByPadreEnColumna(
                    actual.getId(),
                    actual.getEstado(),
                    actualizada.getEstado());
        }

        String nuevoResponsable = actualizada.getResponsableUid();
        String anteriorResponsable = actual.getResponsableUid();
        boolean responsableCambio = nuevoResponsable != null && !nuevoResponsable.isBlank()
                && !nuevoResponsable.equals(anteriorResponsable);
        if (responsableCambio) {
            repository.addNotificacion(
                    nuevoResponsable,
                    "Se te ha asignado la tarea «" + etiquetaTarea(actualizada) + "».");
        }
    }

    private static String normalizarEstado(String estado) {
        return estado == null ? "" : estado.trim().toUpperCase().replace('-', '_');
    }

    private static String etiquetaTarea(Tarea tarea) {
        if (tarea.getLetraSubtarea() != null && !tarea.getLetraSubtarea().isBlank()) {
            return tarea.getLetraSubtarea() + " · " + tarea.getTitulo();
        }
        return tarea.getTitulo();
    }
}
