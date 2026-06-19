package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;
import es.upsa.dasi.tfg.informes.domain.repository.Repository;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.mappers.DaoMappers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
    public InformeResumen findResumenByUsuario(String usuarioUid)
    {
        return mappers.toInformeResumen(dao.selectResumenByUsuario(usuarioUid));
    }
}
