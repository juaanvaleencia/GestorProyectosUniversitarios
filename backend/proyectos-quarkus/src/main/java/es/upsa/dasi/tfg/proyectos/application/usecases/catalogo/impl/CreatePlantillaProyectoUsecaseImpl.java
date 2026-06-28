package es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreatePlantillaProyectoRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaHitoInput;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.PlantillaTareaInput;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.catalogo.CreatePlantillaProyectoUsecase;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CreatePlantillaProyectoUsecaseImpl implements CreatePlantillaProyectoUsecase
{
    @Inject Repository repository;
    @Inject CatalogoAuthorizationService authz;

    @Override
    @Transactional
    public PlantillaProyecto execute(long asignaturaId, CreatePlantillaProyectoRequest request)
            throws NotFoundTfgException
    {
        authz.requireProfesorImparteAsignatura(asignaturaId);

        String titulo = request.getTitulo().trim();
        if (titulo.isEmpty()) {
            throw validation("El título de la plantilla es obligatorio");
        }

        int orden = request.getOrden() != null
                ? request.getOrden()
                : repository.maxOrdenPlantillaByAsignatura(asignaturaId) + 1;

        LocalDate fechaInicio = parseDate(request.getFechaInicioSugerida());
        LocalDate fechaFin = parseDate(request.getFechaFinSugerida());

        long plantillaId = repository.createPlantillaProyecto(
                asignaturaId,
                titulo,
                trimOrNull(request.getDescripcion()),
                orden,
                fechaInicio,
                fechaFin
        );

        List<PlantillaTareaInput> tareas = request.getTareas() != null ? request.getTareas() : List.of();
        int tareaOrden = 1;
        for (PlantillaTareaInput tarea : tareas) {
            if (tarea.getTitulo() == null || tarea.getTitulo().isBlank()) continue;
            int ord = tarea.getOrden() != null ? tarea.getOrden() : tareaOrden++;
            repository.addPlantillaTarea(
                    plantillaId,
                    tarea.getTitulo().trim(),
                    trimOrNull(tarea.getDescripcion()),
                    ord,
                    parseDate(tarea.getFechaLimiteSugerida())
            );
        }

        List<PlantillaHitoInput> hitos = request.getHitos() != null ? request.getHitos() : List.of();
        int hitoOrden = 1;
        for (PlantillaHitoInput hito : hitos) {
            if (hito.getTitulo() == null || hito.getTitulo().isBlank()) continue;
            LocalDate fecha = parseDate(hito.getFechaSugerida());
            if (fecha == null) {
                throw validation("Cada hito debe tener una fecha sugerida");
            }
            int ord = hito.getOrden() != null ? hito.getOrden() : hitoOrden++;
            repository.addPlantillaHito(plantillaId, hito.getTitulo().trim(), fecha, ord);
        }

        return repository.findPlantillaDetalleById(plantillaId)
                .orElseThrow(() -> new NotFoundTfgException("Plantilla no encontrada: " + plantillaId));
    }

    private static String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value);
    }

    private static TfgValidationRuntimeException validation(String message) {
        return new TfgValidationRuntimeException(new ErrorResponse[] {
                ErrorResponse.builder().status("400").message(message).build()
        });
    }
}
