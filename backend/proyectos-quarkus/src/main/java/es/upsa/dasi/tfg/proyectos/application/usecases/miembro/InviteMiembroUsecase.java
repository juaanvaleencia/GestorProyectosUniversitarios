package es.upsa.dasi.tfg.proyectos.application.usecases.miembro;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InviteMiembroCommand;

public interface InviteMiembroUsecase
{
    InvitacionProyecto execute(long proyectoId, InviteMiembroCommand command) throws NotFoundTfgException;
}
