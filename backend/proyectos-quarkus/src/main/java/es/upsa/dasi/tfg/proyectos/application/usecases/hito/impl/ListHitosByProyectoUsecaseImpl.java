package es.upsa.dasi.tfg.proyectos.application.usecases.hito.impl;

import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.ListHitosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
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
        authz.requireViewAccess(proyectoId);
        return repository.findHitosByProyecto(proyectoId);
    }
}

