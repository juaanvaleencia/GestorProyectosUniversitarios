package es.upsa.dasi.tfg.proyectos.application.miembro;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InvitacionProyectoRulesService
{
    @Inject Repository repository;

    public void validateUsuarioPuedeUnirseAProyecto(String usuarioUid, long proyectoId)
    {
        repository.findPlantillaIdByProyectoId(proyectoId).ifPresent(plantillaId -> {
            if (repository.existsMiembroEnOtroProyectoDePlantilla(usuarioUid, plantillaId, proyectoId)
                    || repository.existsPropietarioEnOtroProyectoDePlantilla(usuarioUid, plantillaId, proyectoId)) {
                throw validation("Ya participas en otro proyecto de la misma plantilla");
            }
        });
    }

    private static TfgValidationRuntimeException validation(String message)
    {
        return new TfgValidationRuntimeException(new ErrorResponse[] {
                ErrorResponse.builder().status("400").message(message).build()
        });
    }
}
