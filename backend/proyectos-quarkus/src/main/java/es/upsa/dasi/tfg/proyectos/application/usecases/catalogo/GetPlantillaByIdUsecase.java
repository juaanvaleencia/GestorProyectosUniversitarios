package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;

public interface GetPlantillaByIdUsecase
{
    PlantillaProyecto execute(long plantillaId) throws NotFoundTfgException;
}
