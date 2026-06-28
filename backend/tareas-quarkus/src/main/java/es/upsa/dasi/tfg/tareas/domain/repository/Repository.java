package es.upsa.dasi.tfg.tareas.domain.repository;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    List<Tarea> findByProyectoId(long proyectoId);
    Optional<Tarea> findById(long id);
    Tarea add(Tarea tarea);
    Tarea update(Tarea tarea) throws NotFoundTfgException;
    void deleteById(long id) throws NotFoundTfgException;
    void addNotificacion(String usuarioUid, String texto);
    boolean isUsuarioTutorEnProyecto(long proyectoId, String usuarioUid);
    int countSubtareasByPadre(long tareaPadreId);
    int countSubtareasSinResponsableByPadre(long tareaPadreId);

    void updateEstadoSubtareasByPadreEnColumna(long tareaPadreId, String estadoOrigen, String estadoDestino);
}
