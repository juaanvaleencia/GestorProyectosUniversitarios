package es.upsa.dasi.tfg.proyectos.domain.repository;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.ParticipacionProyecto;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    List<Proyecto> findAllForUser(String usuarioUid);
    Proyecto add(Proyecto proyecto);
    Optional<Proyecto> findById(long id);
    Proyecto update(Proyecto proyecto) throws NotFoundTfgException;
    void deleteById(long id) throws NotFoundTfgException;
    List<Hito> findHitosByProyecto(long proyectoId);
    List<Miembro> findMiembrosByProyecto(long proyectoId);
    List<ParticipacionProyecto> findParticipacionesByUsuario(String usuarioUid);
    void addMiembro(long proyectoId, String usuarioUid, String rol);
    boolean isMember(long proyectoId, String usuarioUid);
    boolean existsProyecto(long id);
}
