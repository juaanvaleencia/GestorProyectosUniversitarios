package es.upsa.dasi.tfg.proyectos.application.usecases;

import es.upsa.dasi.tfg.proyectos.domain.model.ParticipacionProyecto;

import java.util.List;

public interface ListParticipacionesByUsuarioUsecase
{
    List<ParticipacionProyecto> execute();
}
