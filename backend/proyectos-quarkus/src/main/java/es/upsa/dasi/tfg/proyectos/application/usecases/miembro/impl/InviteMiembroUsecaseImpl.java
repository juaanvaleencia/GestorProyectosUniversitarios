package es.upsa.dasi.tfg.proyectos.application.usecases.miembro.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.proyectos.application.miembro.InvitacionProyectoRulesService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.miembro.InviteMiembroUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InviteMiembroCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.UsuarioRegistrado;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InviteMiembroUsecaseImpl implements InviteMiembroUsecase
{
    private static final String TIPO_NOTIF_INVITACION = "INVITACION_PROYECTO";

    Repository repository;
    ProyectoAuthorizationService authz;
    InvitacionProyectoRulesService invitacionRules;

    @Inject
    public InviteMiembroUsecaseImpl(
            Repository repository,
            ProyectoAuthorizationService authz,
            InvitacionProyectoRulesService invitacionRules)
    {
        this.repository = repository;
        this.authz = authz;
        this.invitacionRules = invitacionRules;
    }

    @Override
    @Transactional
    public InvitacionProyecto execute(long proyectoId, InviteMiembroCommand command) throws NotFoundTfgException
    {
        authz.requireProductOwner(proyectoId);
        String invitadoPorUid = authz.currentUid();

        String email = command.getEmail().trim();
        UsuarioRegistrado usuario = repository.findUsuarioByEmail(email)
                .orElseThrow(() -> new NotFoundTfgException(
                        "No existe ningún usuario registrado con el email " + email));

        if (repository.isMiembroOfProyecto(proyectoId, usuario.getFirebaseUid())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("Ese usuario ya forma parte del proyecto")
                            .build()
            });
        }

        if (repository.existsInvitacionPendiente(proyectoId, usuario.getFirebaseUid())) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("Ya hay una invitación pendiente para ese usuario")
                            .build()
            });
        }

        repository.findAsignaturaIdByProyectoId(proyectoId).ifPresent(asignaturaId -> {
            if (!repository.isUsuarioMatriculadoEnAsignatura(usuario.getFirebaseUid(), asignaturaId)) {
                throw new TfgValidationRuntimeException(new ErrorResponse[] {
                        ErrorResponse.builder()
                                .status("400")
                                .message("El usuario debe estar matriculado en la asignatura del proyecto")
                                .build()
                });
            }
        });

        invitacionRules.validateUsuarioPuedeUnirseAProyecto(usuario.getFirebaseUid(), proyectoId);

        InvitacionProyecto invitacion = repository.addInvitacionProyecto(
                proyectoId, usuario.getFirebaseUid(), command.getRol(), invitadoPorUid);

        String tituloProyecto = repository.findById(proyectoId)
                .map(p -> p.getTitulo())
                .orElse("un proyecto");
        String nombreInvitador = repository.findUsuarioByUid(invitadoPorUid)
                .map(u -> u.getNombre())
                .orElse("Un miembro del equipo");
        repository.addNotificacion(
                usuario.getFirebaseUid(),
                nombreInvitador + " te ha invitado al proyecto «" + tituloProyecto + "».",
                TIPO_NOTIF_INVITACION,
                invitacion.getId(),
                proyectoId);

        return invitacion;
    }
}
