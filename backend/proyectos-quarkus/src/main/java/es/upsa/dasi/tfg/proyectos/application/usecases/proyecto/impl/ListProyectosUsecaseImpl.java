package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.impl;

import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.ListProyectosUsecase;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListProyectosUsecaseImpl implements ListProyectosUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;

    @Override
    public List<Proyecto> execute() {
        return repository.findAllForUser(authz.currentUid());
    }
}

