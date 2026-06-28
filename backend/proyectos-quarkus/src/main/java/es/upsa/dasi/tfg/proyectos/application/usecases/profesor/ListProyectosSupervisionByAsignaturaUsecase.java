package es.upsa.dasi.tfg.proyectos.application.usecases.profesor;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow;

import java.util.List;

public interface ListProyectosSupervisionByAsignaturaUsecase
{
    List<ProyectoSupervisionRow> execute(long asignaturaId) throws NotFoundTfgException;
}
