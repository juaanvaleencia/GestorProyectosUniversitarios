package es.upsa.dasi.tfg.proyectos.application.usecases.hito.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.hito.HitoValidationService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.hito.CreateHitoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.AddHitoCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateHitoUsecaseImpl implements CreateHitoUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;
    HitoValidationService hitoValidation;

    @Inject
    public CreateHitoUsecaseImpl(
            Repository repository,
            ProyectoAuthorizationService authz,
            HitoValidationService hitoValidation) {
        this.repository = repository;
        this.authz = authz;
        this.hitoValidation = hitoValidation;
    }

    @Override
    public Hito execute(long proyectoId, AddHitoCommand command) throws NotFoundTfgException {
        authz.requireProductOwner(proyectoId);
        Proyecto proyecto = repository.findById(proyectoId)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyectoId));
        hitoValidation.requireFechaWithinProyecto(proyecto, command.getFecha());
        Hito hito = Hito.builder()
                .proyectoId(proyectoId)
                .titulo(command.getTitulo())
                .fecha(command.getFecha())
                .completado(command.isCompletado())
                .build();
        return repository.addHito(hito);
    }
}
