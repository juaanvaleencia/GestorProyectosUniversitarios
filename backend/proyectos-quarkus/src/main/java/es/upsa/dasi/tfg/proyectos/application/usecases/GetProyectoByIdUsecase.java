package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.common.domain.model.Proyecto;

import java.util.Optional;

public interface GetProyectoByIdUsecase
{
    Optional<Proyecto> execute(long id);
}
