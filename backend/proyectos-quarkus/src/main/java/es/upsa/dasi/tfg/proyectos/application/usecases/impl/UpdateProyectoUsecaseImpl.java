package es.upsa.dasi.tfg.proyectos.application.usecases.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.UpdateProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.application.usecases.mappers.UsecaseMapper;
import es.upsa.dasi.tfg.proyectos.domain.model.ReplaceProyectoCommand;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UpdateProyectoUsecaseImpl implements UpdateProyectoUsecase
{
    Repository repository;
    ProyectoAuthorizationService authz;
    UsecaseMapper usecaseMapper;

    @Inject
    public UpdateProyectoUsecaseImpl(
            Repository repository,
            ProyectoAuthorizationService authz,
            UsecaseMapper usecaseMapper)
    {
        this.repository = repository;
        this.authz = authz;
        this.usecaseMapper = usecaseMapper;
    }

    @Override
    public void execute(long id, ReplaceProyectoCommand command) throws NotFoundTfgException
    {
        authz.requireMember(id);
        Proyecto actual = repository.findById(id)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + id));
        Proyecto actualizado = usecaseMapper.toProyecto(
                id, command, actual.getPropietarioUid(), actual.getCreadoEn(), actual.getActualizadoEn());
        repository.update(actualizado);
    }
}
