package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreatePlantillaProyectoRequest;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;

public interface CreatePlantillaProyectoUsecase
{
    PlantillaProyecto execute(long asignaturaId, CreatePlantillaProyectoRequest request) throws NotFoundTfgException;
}
