package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.RemoveMiembroByIdUsecase;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RemoveMiembroByIdUsecaseImpl implements RemoveMiembroByIdUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;

    @Inject
    public RemoveMiembroByIdUsecaseImpl(Repository repository, ProyectoAuthorizationService authz)
    {
        this.repository = repository;
        this.authz = authz;
    }

    @Override
    public void execute(long proyectoId, long miembroId) throws NotFoundTfgException
    {
        authz.requireProductOwner(proyectoId);

        Miembro miembro = repository.findMiembroById(proyectoId, miembroId)
                .orElseThrow(() -> new NotFoundTfgException("Miembro no encontrado: " + miembroId));

        Proyecto proyecto = repository.findById(proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyectoId));

        if (proyecto.getPropietarioUid().equals(miembro.getUsuarioUid())) {
            throw new ForbiddenTfgException("No se puede eliminar al propietario del proyecto");
        }

        if ("TUTOR".equals(miembro.getRol())) {
            throw new ForbiddenTfgException("No se puede eliminar al tutor del proyecto");
        }

        repository.removeMiembroById(proyectoId, miembroId);
    }
}
