package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;

import java.util.List;

public interface ListPlantillasByAsignaturaUsecase
{
    List<PlantillaProyecto> execute(long asignaturaId) throws NotFoundTfgException;
}
