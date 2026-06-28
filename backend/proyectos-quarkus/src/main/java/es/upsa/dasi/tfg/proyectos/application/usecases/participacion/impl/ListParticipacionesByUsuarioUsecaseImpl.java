package es.upsa.dasi.tfg.proyectos.application.usecases.participacion.impl;

import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.participacion.ListParticipacionesByUsuarioUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListParticipacionesByUsuarioUsecaseImpl implements ListParticipacionesByUsuarioUsecase
{
    @Inject ProyectoAuthorizationService auth;
    @Inject Repository repository;

    @Override
    public List<ParticipacionProyecto> execute() {
        return repository.findParticipacionesByUsuario(auth.currentUid());
    }
}
