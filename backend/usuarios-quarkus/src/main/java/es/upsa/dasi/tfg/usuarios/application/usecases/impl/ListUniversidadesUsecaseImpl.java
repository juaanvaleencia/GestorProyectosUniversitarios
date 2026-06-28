package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListUniversidadesUsecase;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListUniversidadesUsecaseImpl implements ListUniversidadesUsecase
{
    Repository repository;

    @Inject
    public ListUniversidadesUsecaseImpl(Repository repository)
    {
        this.repository = repository;
    }

    @Override
    public List<Universidad> execute()
    {
        return repository.findAllUniversidades();
    }
}
