package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.impl;

import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.ListAsignaturasByUniversidadUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListAsignaturasByUniversidadUsecaseImpl implements ListAsignaturasByUniversidadUsecase
{
    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;
    @Inject ProyectoAuthorizationService proyectoAuthz;

    @Override
    public List<Asignatura> execute(long universidadId)
    {
        authz.requireUniversidadDelUsuario(universidadId);
        String uid = proyectoAuthz.currentUid();
        if (repository.isProfesor(uid)) {
            return repository.findAsignaturasDisponiblesParaProfesor(universidadId, uid);
        }
        return repository.findAsignaturasByUniversidadId(universidadId);
    }
}
