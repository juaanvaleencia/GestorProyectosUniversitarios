package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.proyectos.domain.model.Hito;

import java.util.List;

public interface ListHitosByProyectoUsecase
{
    List<Hito> execute(long proyectoId);
}

