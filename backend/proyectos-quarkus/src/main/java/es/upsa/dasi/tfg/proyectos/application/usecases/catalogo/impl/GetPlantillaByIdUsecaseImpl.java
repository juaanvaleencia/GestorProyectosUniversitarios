package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.GetPlantillaByIdUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPlantillaByIdUsecaseImpl implements GetPlantillaByIdUsecase
{
    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;

    @Override
    public PlantillaProyecto execute(long plantillaId) throws NotFoundTfgException
    {
        authz.requirePlantillaDelUsuario(plantillaId);
        return repository.findPlantillaDetalleById(plantillaId)
                .orElseThrow(() -> new NotFoundTfgException("Plantilla no encontrada: " + plantillaId));
    }
}
