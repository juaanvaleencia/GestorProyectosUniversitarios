package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

public interface CreateProyectoDesdePlantillaUsecase
{
    Proyecto execute(long plantillaId) throws NotFoundTfgException;
}
