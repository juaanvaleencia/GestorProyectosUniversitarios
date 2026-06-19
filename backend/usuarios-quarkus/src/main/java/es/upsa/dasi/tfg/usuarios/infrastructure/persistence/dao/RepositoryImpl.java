package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.mappers.DaoMappers;
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
    public RepositoryImpl(Dao dao, DaoMappers mappers)
    {
        this.dao = dao;
        this.mappers = mappers;
    }

    @Override
    public Optional<Usuario> findByUid(String firebaseUid)
    {
        return dao.selectUsuarioByUid(firebaseUid)
                  .map(mappers::toUsuario);
    }

    @Override
    public Usuario add(Usuario usuario)
    {
        return mappers.toUsuario(dao.insertUsuario(mappers.toUsuarioRow(usuario)));
    }

    @Override
    public List<Notificacion> findNotificacionesByUsuario(String usuarioUid) {
        return dao.selectNotificacionesByUsuario(usuarioUid).stream().map(mappers::toNotificacion).toList();
    }
}
