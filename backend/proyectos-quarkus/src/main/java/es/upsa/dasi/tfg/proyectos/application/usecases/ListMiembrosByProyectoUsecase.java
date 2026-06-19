package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.proyectos.domain.model.Miembro;

import java.util.List;

public interface ListMiembrosByProyectoUsecase
{
    List<Miembro> execute(long proyectoId);
}

