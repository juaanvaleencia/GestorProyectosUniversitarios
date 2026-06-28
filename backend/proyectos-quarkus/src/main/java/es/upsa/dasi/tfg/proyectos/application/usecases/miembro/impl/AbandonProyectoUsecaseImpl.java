package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.AbandonProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AbandonProyectoUsecaseImpl implements AbandonProyectoUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;

    @Override
    @Transactional
    public void execute(long proyectoId) throws NotFoundTfgException
    {
        String uid = authz.currentUid();
        authz.requireMember(proyectoId);

        Proyecto proyecto = repository.findById(proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyectoId));

        if (uid.equals(proyecto.getPropietarioUid())) {
            throw new ForbiddenTfgException("El propietario del proyecto no puede abandonarlo");
        }

        Miembro miembro = repository.findMiembroByUsuarioUid(proyectoId, uid)
                .orElseThrow(() -> new NotFoundTfgException("No eres miembro de este proyecto"));

        if ("TUTOR".equals(miembro.getRol())) {
            throw new ForbiddenTfgException("El tutor no puede abandonar el proyecto");
        }

        repository.removeMiembroById(proyectoId, miembro.getId());
    }
}
