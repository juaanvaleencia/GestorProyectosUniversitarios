package es.upsa.dasi.tfg.proyectos.application.usecases.profesor.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListProyectosSupervisionByAsignaturaUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListProyectosSupervisionByAsignaturaUsecaseImpl implements ListProyectosSupervisionByAsignaturaUsecase
{
    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;

    @Override
    public List<ProyectoSupervisionRow> execute(long asignaturaId) throws NotFoundTfgException
    {
        authz.requireProfesorImparteAsignatura(asignaturaId);
        return repository.findProyectosSupervisionByAsignaturaId(asignaturaId);
    }
}
