package es.upsa.dasi.tfg.proyectos.application.usecases.hito.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.hito.HitoValidationService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.UpdateHitoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.ReplaceHitoCommand;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UpdateHitoUsecaseImpl implements UpdateHitoUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;
    HitoValidationService hitoValidation;

    @Inject
    public UpdateHitoUsecaseImpl(
            Repository repository,
            ProyectoAuthorizationService authz,
            HitoValidationService hitoValidation) {
        this.repository = repository;
        this.authz = authz;
        this.hitoValidation = hitoValidation;
    }

    @Override
    public void execute(long proyectoId, long hitoId, ReplaceHitoCommand command) throws NotFoundTfgException {
        authz.requireProductOwner(proyectoId);
        Proyecto proyecto = repository.findById(proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyectoId));
        hitoValidation.requireFechaWithinProyecto(proyecto, command.getFecha());
        Hito actual = repository.findHitoById(proyectoId, hitoId)
                .orElseThrow(() -> new NotFoundTfgException("Hito no encontrado: " + hitoId));
        Hito actualizado = Hito.builder()
                .id(actual.getId())
                .proyectoId(actual.getProyectoId())
                .titulo(command.getTitulo())
                .fecha(command.getFecha())
                .completado(command.isCompletado())
                .build();
        repository.updateHito(actualizado);
    }
}
