package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo;

import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;

import java.util.List;

public interface ListAsignaturasByUniversidadUsecase
{
    List<Asignatura> execute(long universidadId);
}
