package es.upsa.dasi.tfg.proyectos.application.proyecto;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class ProyectoAuthorizationService
{
    @Inject JsonWebToken jwt;
    @Inject Repository repository;

    public String currentUid() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new ForbiddenTfgException("Usuario no autenticado");
        }
        return jwt.getSubject();
    }

    public void requireMember(long proyectoId) {
        if (!repository.isMember(proyectoId, currentUid())) {
            throw new ForbiddenTfgException("No tienes acceso a este proyecto");
        }
    }

    public void requireProductOwner(long proyectoId) {
        if (!repository.isProductOwner(proyectoId, currentUid())) {
            throw new ForbiddenTfgException("Solo el Product Owner puede modificar este proyecto");
        }
    }

    public void requireViewAccess(long proyectoId) {
        String uid = currentUid();
        if (repository.isMember(proyectoId, uid)) {
            return;
        }
        if (repository.isProfesorSupervisorOfProyecto(uid, proyectoId)) {
            return;
        }
        throw new ForbiddenTfgException("No tienes acceso a este proyecto");
    }

    public void requireProfesorDeAsignatura(long asignaturaId) {
        if (!repository.isProfesorDeAsignatura(currentUid(), asignaturaId)) {
            throw new ForbiddenTfgException("No impartes esta asignatura");
        }
    }
}
