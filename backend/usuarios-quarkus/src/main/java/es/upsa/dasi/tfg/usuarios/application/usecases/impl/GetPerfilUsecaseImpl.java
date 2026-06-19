package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.GetPerfilUsecase;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPerfilUsecaseImpl implements GetPerfilUsecase
{
    UsuarioAuthenticationService auth;

    @Inject
    public GetPerfilUsecaseImpl(UsuarioAuthenticationService auth)
    {
        this.auth = auth;
    }

    @Override
    public Usuario execute()
    {
        return auth.resolveCurrentUsuario();
    }
}
