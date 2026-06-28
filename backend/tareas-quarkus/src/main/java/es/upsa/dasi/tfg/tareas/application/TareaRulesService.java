package es.upsa.dasi.tfg.tareas.application;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TareaRulesService
{
    @Inject Repository repository;

    public void validateResponsable(long proyectoId, String responsableUid)
    {
        if (responsableUid == null || responsableUid.isBlank()) {
            return;
        }
        if (repository.isUsuarioTutorEnProyecto(proyectoId, responsableUid)) {
            throw validation("El tutor no puede ser responsable de tareas");
        }
    }

    public void validateEstadoConResponsable(String estado, String responsableUid)
    {
        if ("PENDIENTE".equals(estado)) {
            return;
        }
        if (responsableUid == null || responsableUid.isBlank()) {
            throw validation("Asigna un responsable antes de mover la tarea fuera de Pendiente");
        }
    }

    public void validateTareaUpdate(Tarea actual, Tarea actualizada)
    {
        validateResponsable(actual.getProyectoId(), actualizada.getResponsableUid());

        boolean esSubtarea = actual.getTareaPadreId() != null;
        boolean padreProfesor = esPadreProfesor(actual);
        int subtareas = padreProfesor ? repository.countSubtareasByPadre(actual.getId()) : 0;

        if (esSubtarea) {
            validateEstadoConResponsable(actualizada.getEstado(), actualizada.getResponsableUid());
        } else if (!padreProfesor || subtareas == 0) {
            validateEstadoConResponsable(actualizada.getEstado(), actualizada.getResponsableUid());
        }

        if (padreProfesor && subtareas > 0 && estadoCambio(actual, actualizada)) {
            if (repository.countSubtareasSinResponsableByPadre(actual.getId()) > 0) {
                throw validation("Todas las subtareas deben tener responsable antes de mover la tarea principal");
            }
        }

        if ("PROFESOR".equals(actual.getOrigen()) && actual.getTareaPadreId() == null) {
            if (!actual.getTitulo().equals(actualizada.getTitulo())) {
                throw validation("No se puede modificar el título de una tarea del profesor");
            }
        }
    }

    private static boolean estadoCambio(Tarea actual, Tarea actualizada)
    {
        String anterior = actual.getEstado() == null ? "" : actual.getEstado().trim().toUpperCase().replace('-', '_');
        String nuevo = actualizada.getEstado() == null ? "" : actualizada.getEstado().trim().toUpperCase().replace('-', '_');
        return !anterior.equals(nuevo);
    }

    public void validateCanDelete(Tarea tarea)
    {
        if ("PROFESOR".equals(tarea.getOrigen()) && tarea.getTareaPadreId() == null) {
            throw validation("No se pueden eliminar las tareas definidas por el profesor");
        }
    }

    public Tarea validateSubtareaParent(long proyectoId, long tareaPadreId)
    {
        Tarea padre = repository.findById(tareaPadreId)
                .orElseThrow(() -> validation("Tarea padre no encontrada"));
        if (padre.getProyectoId() != proyectoId) {
            throw validation("La tarea padre no pertenece a este proyecto");
        }
        if (!"PROFESOR".equals(padre.getOrigen()) || padre.getTareaPadreId() != null) {
            throw validation("Solo se pueden crear subtareas bajo tareas del profesor");
        }
        return padre;
    }

    public boolean esPadreProfesor(Tarea tarea)
    {
        return "PROFESOR".equals(tarea.getOrigen()) && tarea.getTareaPadreId() == null;
    }

    public String siguienteLetraSubtarea(long tareaPadreId)
    {
        int count = repository.countSubtareasByPadre(tareaPadreId);
        if (count >= 26) {
            throw validation("Máximo 26 subtareas por tarea");
        }
        return String.valueOf((char) ('A' + count));
    }

    private static TfgValidationRuntimeException validation(String message)
    {
        return new TfgValidationRuntimeException(new ErrorResponse[] {
                ErrorResponse.builder().status("400").message(message).build()
        });
    }
}
