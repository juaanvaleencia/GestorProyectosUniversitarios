package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.impl;

import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.GetProyectoByIdUsecase;
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
        authz.requireViewAccess(id);
        return repository.findById(id);
    }
}
