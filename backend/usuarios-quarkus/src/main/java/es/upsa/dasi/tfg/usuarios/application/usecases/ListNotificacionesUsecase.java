package es.upsa.dasi.tfg.usuarios.application.usecases;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;

import java.util.List;

public interface ListNotificacionesUsecase
{
    List<Notificacion> execute();
}
