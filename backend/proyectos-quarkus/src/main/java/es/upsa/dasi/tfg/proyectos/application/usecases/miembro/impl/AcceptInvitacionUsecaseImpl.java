package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.proyectos.application.miembro.InvitacionProyectoRulesService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.AcceptInvitacionUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AcceptInvitacionUsecaseImpl implements AcceptInvitacionUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;
    @Inject InvitacionProyectoRulesService invitacionRules;

    @Override
    @Transactional
    public void execute(long invitacionId) throws NotFoundTfgException
    {
        String uid = authz.currentUid();
        InvitacionProyecto invitacion = repository.findInvitacionById(invitacionId)
                .orElseThrow(() -> new NotFoundTfgException("Invitación no encontrada: " + invitacionId));

        if (!uid.equals(invitacion.getUsuarioUid())) {
            throw new ForbiddenTfgException("No puedes aceptar esta invitación");
        }
        if (!"PENDIENTE".equals(invitacion.getEstado())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder().status("400").message("La invitación ya no está pendiente").build()
            });
        }

        if (repository.isMiembroOfProyecto(invitacion.getProyectoId(), uid)) {
            repository.updateInvitacionEstado(invitacionId, "ACEPTADA");
            return;
        }

        invitacionRules.validateUsuarioPuedeUnirseAProyecto(uid, invitacion.getProyectoId());

        repository.addMiembro(invitacion.getProyectoId(), uid, invitacion.getRol());
        if (!repository.updateInvitacionEstado(invitacionId, "ACEPTADA")) {
            throw new NotFoundTfgException("Invitación no encontrada: " + invitacionId);
        }
    }
}
