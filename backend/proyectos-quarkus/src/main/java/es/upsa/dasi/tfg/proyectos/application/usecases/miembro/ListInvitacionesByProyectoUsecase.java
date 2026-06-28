package es.upsa.dasi.tfg.proyectos.application.usecases.miembro;

import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;

import java.util.List;

public interface ListInvitacionesByProyectoUsecase
{
    List<InvitacionProyecto> execute(long proyectoId);
}
