package es.upsa.dasi.tfg.proyectos.application.usecases.profesor.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoParticipacionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.model.RolProyecto;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListParticipacionesAlumnoForProfesorUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListParticipacionesAlumnoForProfesorUsecaseImpl implements ListParticipacionesAlumnoForProfesorUsecase
{
    @Inject ProyectoAuthorizationService authz;
    @Inject Repository repository;

    @Override
    public List<ProyectoParticipacionResponse> execute(String alumnoUid) throws ForbiddenTfgException
    {
        String profesorUid = authz.currentUid();
        return repository.findParticipacionesAlumnoForProfesor(profesorUid, alumnoUid).stream()
                .map(p -> ProyectoParticipacionResponse.builder()
                        .proyectoId(p.getProyectoId())
                        .titulo(p.getTitulo())
                        .estado(p.getEstado())
                        .rol(p.getRol())
                        .rolEtiqueta(RolProyecto.etiquetaDe(p.getRol()))
                        .build())
                .toList();
    }
}
