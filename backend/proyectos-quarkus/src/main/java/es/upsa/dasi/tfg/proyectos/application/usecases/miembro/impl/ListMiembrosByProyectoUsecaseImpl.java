package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.ListMiembrosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListMiembrosByProyectoUsecaseImpl implements ListMiembrosByProyectoUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;

    @Override
    public List<Miembro> execute(long proyectoId) {
        authz.requireViewAccess(proyectoId);
        return repository.findMiembrosByProyecto(proyectoId);
    }
}

