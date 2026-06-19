package es.upsa.dasi.tfg.usuarios.application;

import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class UsuarioAuthenticationService
{
    @Inject JsonWebToken jwt;
    @Inject Repository repository;

    public String getCurrentUid()
    {
        return jwt.getSubject();
    }

    public String claimAsString(String name)
    {
        Object value = jwt.getClaim(name);
        return value == null ? null : String.valueOf(value);
    }

    public Usuario resolveCurrentUsuario()
    {
        String uid = getCurrentUid();
        return repository.findByUid(uid).orElseGet(() -> {
            String email = claimAsString("email");
            String nombre = claimAsString("name");
            if (email == null || email.isBlank()) {
                email = uid + "@upsa.es";
            }
            if (nombre == null || nombre.isBlank()) {
                nombre = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            }
            return repository.add(Usuario.builder()
                    .firebaseUid(uid)
                    .email(email)
                    .nombre(nombre)
                    .build());
        });
    }
}
