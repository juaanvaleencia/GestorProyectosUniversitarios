package es.upsa.dasi.tfg.proyectos.adapters.rest.participacion;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoParticipacionResponse;
import es.upsa.dasi.tfg.common.domain.model.RolProyecto;
import es.upsa.dasi.tfg.proyectos.application.usecases.participacion.ListParticipacionesByUsuarioUsecase;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Participaciones", description = "Participaciones del usuario en proyectos")
@Path("/api/usuarios/me/participaciones")
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioParticipacionesResource
{
    @Inject ListParticipacionesByUsuarioUsecase listParticipaciones;

    @GET
    public List<ProyectoParticipacionResponse> list() {
        return listParticipaciones.execute().stream().map(this::toResponse).toList();
    }

    private ProyectoParticipacionResponse toResponse(ParticipacionProyecto p) {
        return ProyectoParticipacionResponse.builder()
                .proyectoId(p.getProyectoId())
                .titulo(p.getTitulo())
                .estado(p.getEstado())
                .rol(p.getRol())
                .rolEtiqueta(RolProyecto.etiquetaDe(p.getRol()))
                .build();
    }
}
