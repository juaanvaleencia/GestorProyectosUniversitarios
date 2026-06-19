package es.upsa.dasi.tfg.proyectos.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.GetProyectoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class GetProyectoByIdUsecaseImpl implements GetProyectoByIdUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;

    @Inject
    public GetProyectoByIdUsecaseImpl(Repository repository, ProyectoAuthorizationService authz)
    {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public Optional<Proyecto> execute(long id)
    {
        authz.requireMember(id);
        return repository.findById(id);
    }
}
