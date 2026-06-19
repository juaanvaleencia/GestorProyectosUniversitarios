package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuariosResource
{
    Repository repository;

    @Inject
    public UsuariosResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    @Path("perfil")
    public UsuarioPerfilResponse perfil() throws TfgException {
        return repository.findPerfilUsuario();
    }

    @POST
    @Path("sync")
    public Response sync(@Valid UsuarioSync request) throws TfgException {
        return repository.syncUsuario(request);
    }
}
