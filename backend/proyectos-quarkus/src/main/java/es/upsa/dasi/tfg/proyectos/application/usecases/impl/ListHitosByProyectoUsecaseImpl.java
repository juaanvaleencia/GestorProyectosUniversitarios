package es.upsa.dasi.tfg.proyectos.application.usecases.impl;

import es.upsa.dasi.tfg.proyectos.application.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.ListHitosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.Hito;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListHitosByProyectoUsecaseImpl implements ListHitosByProyectoUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;

    @Override
    public List<Hito> execute(long proyectoId) {
        authz.requireMember(proyectoId);
        return repository.findHitosByProyecto(proyectoId);
    }
}

