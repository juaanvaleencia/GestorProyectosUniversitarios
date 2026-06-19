package es.upsa.dasi.tfg.usuarios.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.usuarios.adapters.rest.dtos.UsuarioSyncRequest;
import es.upsa.dasi.tfg.usuarios.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.usuarios.application.usecases.GetPerfilUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.SyncUsuarioUsecase;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuariosResource
{
    GetPerfilUsecase getPerfilUsecase;
    SyncUsuarioUsecase syncUsuarioUsecase;
    ResponseMappers responseMappers;
    Validator validator;

    @Inject
    public UsuariosResource(
            GetPerfilUsecase getPerfilUsecase,
            SyncUsuarioUsecase syncUsuarioUsecase,
            ResponseMappers responseMappers,
            Validator validator)
    {
        this.getPerfilUsecase = getPerfilUsecase;
        this.syncUsuarioUsecase = syncUsuarioUsecase;
        this.responseMappers = responseMappers;
        this.validator = validator;
    }

    @GET
    @Path("perfil")
    public UsuarioPerfilResponse perfil()
    {
        Usuario usuario = getPerfilUsecase.execute();
        return responseMappers.toUsuarioPerfilResponse(usuario);
    }

    @POST
    @Path("sync")
    public Response sync(UsuarioSyncRequest request)
    {
        Set<ConstraintViolation<UsuarioSyncRequest>> violationSet = validator.validate(request);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        Usuario usuario = syncUsuarioUsecase.execute(responseMappers.toSyncUsuarioCommand(request));
        return Response.ok(responseMappers.toUsuarioPerfilResponse(usuario)).build();
    }
}
