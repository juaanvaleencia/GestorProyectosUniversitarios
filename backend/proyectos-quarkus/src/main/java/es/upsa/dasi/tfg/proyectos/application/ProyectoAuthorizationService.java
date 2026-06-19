package es.upsa.dasi.tfg.proyectos.application;

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
}

