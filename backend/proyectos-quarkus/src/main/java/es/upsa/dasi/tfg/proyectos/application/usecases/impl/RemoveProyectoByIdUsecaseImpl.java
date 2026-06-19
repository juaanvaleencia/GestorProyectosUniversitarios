package es.upsa.dasi.tfg.proyectos.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.RemoveProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveProyectoByIdUsecaseImpl implements RemoveProyectoByIdUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;

    @Inject
    public RemoveProyectoByIdUsecaseImpl(Repository repository, ProyectoAuthorizationService authz)
    {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public void execute(long id) throws NotFoundTfgException
    {
        authz.requireMember(id);
        repository.deleteById(id);
    }
}
