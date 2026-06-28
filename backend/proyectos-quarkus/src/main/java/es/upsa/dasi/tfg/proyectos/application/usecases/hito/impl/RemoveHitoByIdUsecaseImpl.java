package es.upsa.dasi.tfg.proyectos.application.usecases.hito.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.RemoveHitoByIdUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveHitoByIdUsecaseImpl implements RemoveHitoByIdUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;

    @Inject
    public RemoveHitoByIdUsecaseImpl(Repository repository, ProyectoAuthorizationService authz) {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public void execute(long proyectoId, long hitoId) throws NotFoundTfgException {
        authz.requireProductOwner(proyectoId);
        repository.removeHitoById(proyectoId, hitoId);
    }
}
