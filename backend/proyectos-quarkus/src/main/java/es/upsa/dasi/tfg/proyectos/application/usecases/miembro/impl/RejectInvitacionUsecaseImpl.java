package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.RejectInvitacionUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RejectInvitacionUsecaseImpl implements RejectInvitacionUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;

    @Override
    public void execute(long invitacionId) throws NotFoundTfgException
    {
        String uid = authz.currentUid();
        InvitacionProyecto invitacion = repository.findInvitacionById(invitacionId)
                .orElseThrow(() -> new NotFoundTfgException("Invitación no encontrada: " + invitacionId));

        if (!uid.equals(invitacion.getUsuarioUid())) {
            throw new ForbiddenTfgException("No puedes rechazar esta invitación");
        }
        if (!"PENDIENTE".equals(invitacion.getEstado())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("La invitación ya no está pendiente").build()
            });
        }
        if (!repository.updateInvitacionEstado(invitacionId, "RECHAZADA")) {
            throw new NotFoundTfgException("Invitación no encontrada: " + invitacionId);
        }
    }
}
