package es.upsa.dasi.tfg.proyectos.application.usecases.participacion;

import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;

import java.util.List;

public interface ListParticipacionesByUsuarioUsecase
{
    List<ParticipacionProyecto> execute();
}
