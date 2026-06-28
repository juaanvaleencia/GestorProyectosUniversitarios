package es.upsa.dasi.tfg.proyectos.application.usecases.profesor.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ParticipanteSupervisionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoGrupoSupervisionResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.model.RolProyecto;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.profesor.ListGruposByPlantillaUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.GrupoSupervisionMemberRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ListGruposByPlantillaUsecaseImpl implements ListGruposByPlantillaUsecase
{
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;

    @Override
    public List<ProyectoGrupoSupervisionResponse> execute(long plantillaId) throws NotFoundTfgException
    {
        long asignaturaId = repository.findAsignaturaIdByPlantillaId(plantillaId)
                .orElseThrow(() -> new NotFoundTfgException("Plantilla no encontrada: " + plantillaId));
        authz.requireProfesorImparteAsignatura(asignaturaId);

        Map<Long, ProyectoGrupoSupervisionResponse> grupos = new LinkedHashMap<>();
        for (GrupoSupervisionMemberRow row : repository.findGruposByPlantillaId(plantillaId)) {
            ProyectoGrupoSupervisionResponse grupo = grupos.computeIfAbsent(row.getProyectoId(), id ->
                    ProyectoGrupoSupervisionResponse.builder()
                            .id(row.getProyectoId())
                            .titulo(row.getProyectoTitulo())
                            .estado(row.getProyectoEstado())
                            .fechaInicio(formatDate(row.getFechaInicio()))
                            .fechaFin(formatDate(row.getFechaFin()))
                            .actualizadoEn(row.getActualizadoEn() != null ? row.getActualizadoEn().toString() : null)
                            .participantes(new ArrayList<>())
                            .build()
            );

            if (row.getMiembroUid() != null && row.getMiembroNombre() != null) {
                boolean yaIncluido = grupo.getParticipantes().stream()
                        .anyMatch(p -> row.getMiembroEmail() != null && row.getMiembroEmail().equals(p.getEmail()));
                if (!yaIncluido) {
                    grupo.getParticipantes().add(ParticipanteSupervisionResponse.builder()
                            .uid(row.getMiembroUid())
                            .nombre(row.getMiembroNombre())
                            .email(row.getMiembroEmail())
                            .rol(row.getMiembroRol())
                            .rolEtiqueta(RolProyecto.etiquetaDe(row.getMiembroRol()))
                            .propietario(row.getPropietarioUid() != null
                                    && row.getPropietarioUid().equals(row.getMiembroUid()))
                            .build());
                }
            }
        }

        return new ArrayList<>(grupos.values());
    }

    private static String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(ISO) : null;
    }
}
