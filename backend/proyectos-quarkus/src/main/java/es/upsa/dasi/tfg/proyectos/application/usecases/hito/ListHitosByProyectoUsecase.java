package es.upsa.dasi.tfg.proyectos.application.usecases.hito;

import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;

import java.util.List;

public interface ListHitosByProyectoUsecase
{
    List<Hito> execute(long proyectoId);
}

