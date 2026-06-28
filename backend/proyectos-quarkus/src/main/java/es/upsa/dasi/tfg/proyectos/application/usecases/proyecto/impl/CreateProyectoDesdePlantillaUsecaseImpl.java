package es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.impl;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.application.catalogo.CatalogoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.application.usecases.proyecto.CreateProyectoDesdePlantillaUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaHito;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaTarea;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateProyectoDesdePlantillaUsecaseImpl implements CreateProyectoDesdePlantillaUsecase
{
    @Inject Repository repository;
    @Inject ProyectoAuthorizationService authz;
    @Inject CatalogoAuthorizationService catalogoAuthz;

    @Override
    @Transactional
    public Proyecto execute(long plantillaId) throws NotFoundTfgException
    {
        catalogoAuthz.requirePlantillaDelUsuario(plantillaId);
        String uid = authz.currentUid();

        if (repository.existsMiembroEnProyectoDePlantilla(uid, plantillaId)) {
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("Ya participas en un proyecto creado con esta plantilla")
                            .build()
            });
        }

        PlantillaProyecto plantilla = repository.findPlantillaDetalleById(plantillaId)
                .orElseThrow(() -> new NotFoundTfgException("Plantilla no encontrada: " + plantillaId));

        Proyecto creado = repository.createFromPlantilla(plantilla, uid);
        long proyectoId = creado.getId();

        repository.addMiembro(proyectoId, uid, "PRODUCT_OWNER");

        repository.findTutorDemoUidByPlantillaId(plantillaId)
                .filter(tutorUid -> !tutorUid.isBlank())
                .ifPresent(tutorUid -> repository.addMiembro(proyectoId, tutorUid, "TUTOR"));

        for (PlantillaHito plantillaHito : nullSafeHitos(plantilla)) {
            repository.addHito(Hito.builder()
                    .proyectoId(proyectoId)
                    .titulo(plantillaHito.getTitulo())
                    .fecha(plantillaHito.getFechaSugerida())
                    .completado(false)
                    .build());
        }

        for (PlantillaTarea plantillaTarea : nullSafeTareas(plantilla)) {
            repository.addTareaFromPlantilla(
                    proyectoId,
                    plantillaTarea.getTitulo(),
                    plantillaTarea.getDescripcion(),
                    plantillaTarea.getOrden() != null ? plantillaTarea.getOrden() : 0,
                    plantillaTarea.getFechaLimiteSugerida());
        }

        return creado;
    }

    private static java.util.List<PlantillaHito> nullSafeHitos(PlantillaProyecto plantilla) {
        return plantilla.getHitos() != null ? plantilla.getHitos() : java.util.List.of();
    }

    private static java.util.List<PlantillaTarea> nullSafeTareas(PlantillaProyecto plantilla) {
        return plantilla.getTareas() != null ? plantilla.getTareas() : java.util.List.of();
    }
}
