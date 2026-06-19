package es.upsa.dasi.tfg.proyectos.application.usecases.impl;

import es.upsa.dasi.tfg.proyectos.application.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.ListMiembrosByProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.Miembro;
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
        authz.requireMember(proyectoId);
        return repository.findMiembrosByProyecto(proyectoId);
    }
}

