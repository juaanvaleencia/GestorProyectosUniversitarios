package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.InvitacionProyectoResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Invitaciones", description = "Invitar miembros a un proyecto")
@Path("/api/proyectos/{proyectoId}/invitaciones")
@Produces(MediaType.APPLICATION_JSON)
public class ProyectoInvitacionesResource
{
    Repository repository;

    @Inject
    public ProyectoInvitacionesResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public List<InvitacionProyectoResponse> list(@PathParam("proyectoId") long proyectoId) throws TfgException {
        return repository.findInvitacionesByProyecto(proyectoId);
    }
}
