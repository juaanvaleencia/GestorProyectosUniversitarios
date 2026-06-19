package es.upsa.dasi.tfg.usuarios.application.usecases.impl;

import es.upsa.dasi.tfg.usuarios.application.UsuarioAuthenticationService;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListNotificacionesUsecase;
import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListNotificacionesUsecaseImpl implements ListNotificacionesUsecase
{
    Repository repository;
    UsuarioAuthenticationService auth;

    @Inject
    public ListNotificacionesUsecaseImpl(Repository repository, UsuarioAuthenticationService auth)
    {
        this.repository = repository;
        this.auth = auth;
    }

    @Override
    public List<Notificacion> execute()
    {
        return repository.findNotificacionesByUsuario(auth.getCurrentUid());
    }
}
