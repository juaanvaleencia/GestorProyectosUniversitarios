package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.ParticipacionProyecto;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.mappers.DaoMappers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositoryImpl implements Repository
{
    private final Dao dao;
    private final DaoMappers mappers;

    @Inject
    public RepositoryImpl(Dao dao, DaoMappers mappers) {
        this.dao = dao;
        this.mappers = mappers;
    }

    @Override
    public List<Proyecto> findAllForUser(String usuarioUid) {
        return dao.selectProyectosByUsuario(usuarioUid).stream().map(mappers::toProyecto).toList();
    }

    @Override
    public Proyecto add(Proyecto proyecto) {
        return mappers.toProyecto(dao.insertProyecto(mappers.toProyectoRow(proyecto)));
    }

    @Override
    public Optional<Proyecto> findById(long id) {
        return dao.selectProyectoById(id).map(mappers::toProyecto);
    }

    @Override
    public Proyecto update(Proyecto proyecto) throws NotFoundTfgException {
        return dao.updateProyecto(mappers.toProyectoRow(proyecto))
                .map(mappers::toProyecto)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyecto.getId()));
    }

    @Override
    public void deleteById(long id) throws NotFoundTfgException {
        if (dao.deleteProyectoById(id) == 0) {
            throw new NotFoundTfgException("Proyecto no encontrado: " + id);
        }
    }

    @Override
    public List<Hito> findHitosByProyecto(long proyectoId) {
        return dao.selectHitosByProyecto(proyectoId).stream().map(mappers::toHito).toList();
    }

    @Override
    public List<Miembro> findMiembrosByProyecto(long proyectoId) {
        return dao.selectMiembrosByProyecto(proyectoId).stream().map(mappers::toMiembro).toList();
    }

    @Override
    public List<ParticipacionProyecto> findParticipacionesByUsuario(String usuarioUid) {
        return dao.selectParticipacionesByUsuario(usuarioUid).stream().map(mappers::toParticipacion).toList();
    }

    @Override
    public void addMiembro(long proyectoId, String usuarioUid, String rol) {
        dao.insertMiembro(proyectoId, usuarioUid, rol);
    }

    @Override
    public boolean isMember(long proyectoId, String usuarioUid) {
        return dao.existsMiembro(proyectoId, usuarioUid);
    }

    @Override
    public boolean existsProyecto(long id) {
        return dao.selectProyectoById(id).isPresent();
    }
}
