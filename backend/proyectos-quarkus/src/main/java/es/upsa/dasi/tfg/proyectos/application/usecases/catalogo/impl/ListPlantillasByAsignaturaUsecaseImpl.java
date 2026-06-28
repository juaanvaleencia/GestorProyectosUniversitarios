package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.ListPlantillasByAsignaturaUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListPlantillasByAsignaturaUsecaseImpl implements ListPlantillasByAsignaturaUsecase
{
    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;
    @Inject ProyectoAuthorizationService proyectoAuthz;

    @Override
    public List<PlantillaProyecto> execute(long asignaturaId) throws NotFoundTfgException
    {
        authz.requireAsignaturaAccesible(asignaturaId);
        String uid = proyectoAuthz.currentUid();
        List<PlantillaProyecto> plantillas = repository.findPlantillasByAsignaturaId(asignaturaId);
        if (repository.isProfesorDeAsignatura(uid, asignaturaId)) {
            return plantillas;
        }
        return plantillas.stream()
                .filter(p -> !repository.existsMiembroEnProyectoDePlantilla(uid, p.getId()))
                .toList();
    }
}
