package es.upsa.dasi.tfg.proyectos.application.usecases.miembro;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;

public interface AcceptInvitacionUsecase
{
    void execute(long invitacionId) throws NotFoundTfgException;
}
