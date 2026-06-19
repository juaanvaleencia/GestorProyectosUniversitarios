package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ProyectoRow;

import java.util.List;
import java.util.Optional;

public interface Dao
{
    List<ProyectoRow> selectProyectosByUsuario(String usuarioUid);
    Optional<ProyectoRow> selectProyectoById(long id);
    ProyectoRow insertProyecto(ProyectoRow row);
    Optional<ProyectoRow> updateProyecto(ProyectoRow row);
    int deleteProyectoById(long id);
    List<HitoRow> selectHitosByProyecto(long proyectoId);
    List<ParticipacionRow> selectParticipacionesByUsuario(String usuarioUid);
    List<MiembroRow> selectMiembrosByProyecto(long proyectoId);
    boolean existsMiembro(long proyectoId, String usuarioUid);
    MiembroRow insertMiembro(long proyectoId, String usuarioUid, String rol);
}
