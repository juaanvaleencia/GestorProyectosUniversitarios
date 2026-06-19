package es.upsa.dasi.tfg.informes.application.usecases.impl;

import es.upsa.dasi.tfg.informes.application.usecases.ListInformesResumenUsecase;
import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;
import es.upsa.dasi.tfg.informes.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListInformesResumenUsecaseImpl implements ListInformesResumenUsecase
{
    Repository repository;

    @Inject
    public ListInformesResumenUsecaseImpl(Repository repository)
    {
        this.repository = repository;
    }

    @Override
    public InformeResumen execute(String usuarioUid)
    {
        return repository.findResumenByUsuario(usuarioUid);
    }
}
