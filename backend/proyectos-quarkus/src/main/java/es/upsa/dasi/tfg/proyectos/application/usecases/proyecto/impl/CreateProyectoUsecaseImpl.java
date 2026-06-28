package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.impl;

import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.CreateProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.mappers.UsecaseMapper;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.AddProyectoCommand;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateProyectoUsecaseImpl implements CreateProyectoUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;
    UsecaseMapper usecaseMapper;

    @Inject
    public CreateProyectoUsecaseImpl(
            Repository repository,
            ProyectoAuthorizationService authz,
            UsecaseMapper usecaseMapper) {
        this.repository = repository;
        this.authz = authz;
        this.usecaseMapper = usecaseMapper;
    }

    @Override
    public Proyecto execute(AddProyectoCommand command) {
        String uid = authz.currentUid();
        Proyecto nuevo = usecaseMapper.toProyecto(command, uid);
        Proyecto creado = repository.add(nuevo);
        repository.addMiembro(creado.getId(), uid, "PRODUCT_OWNER");
        return creado;
    }
}
