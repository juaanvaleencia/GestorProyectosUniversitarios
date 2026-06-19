package es.upsa.dasi.tfg.informes.application;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class InformesAuthenticationService
{
    @Inject JsonWebToken jwt;
    @Inject SecurityIdentity securityIdentity;

    public String getCurrentUid()
    {
        if (jwt != null && jwt.getSubject() != null && !jwt.getSubject().isBlank()) {
            return jwt.getSubject();
        }
        if (securityIdentity != null && !securityIdentity.isAnonymous()) {
            return securityIdentity.getPrincipal().getName();
        }
        throw new ForbiddenTfgException("Usuario no autenticado");
    }
}
