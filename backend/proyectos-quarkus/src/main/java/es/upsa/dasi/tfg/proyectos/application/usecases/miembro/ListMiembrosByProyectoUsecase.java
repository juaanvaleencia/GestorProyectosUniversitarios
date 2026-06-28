package es.upsa.dasi.tfg.proyectos.application.usecases.miembro;

import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;

import java.util.List;

public interface ListMiembrosByProyectoUsecase
{
    List<Miembro> execute(long proyectoId);
}

