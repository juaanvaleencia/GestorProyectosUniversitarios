package es.upsa.dasi.tfg.usuarios.adapters.rest;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ActualizarMatriculasRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreateAsignaturaProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.RegistroProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.usuarios.adapters.rest.dtos.UsuarioSyncRequest;
import es.upsa.dasi.tfg.usuarios.adapters.rest.mappers.ResponseMappers;
import es.upsa.dasi.tfg.usuarios.application.usecases.CreateAsignaturaProfesorUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.GetPerfilUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.ListMatriculasUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.RegisterProfesorUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.SyncUsuarioUsecase;
import es.upsa.dasi.tfg.usuarios.application.usecases.UpdateMatriculasUsecase;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Usuarios", description = "Perfil, matrículas y datos del usuario")
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuariosResource
{
    GetPerfilUsecase getPerfilUsecase;
    SyncUsuarioUsecase syncUsuarioUsecase;
    ListMatriculasUsecase listMatriculasUsecase;
    UpdateMatriculasUsecase updateMatriculasUsecase;
    RegisterProfesorUsecase registerProfesorUsecase;
    CreateAsignaturaProfesorUsecase createAsignaturaProfesorUsecase;
    ResponseMappers responseMappers;
    Validator validator;

    @Inject
    public UsuariosResource(
            GetPerfilUsecase getPerfilUsecase,
            SyncUsuarioUsecase syncUsuarioUsecase,
            ListMatriculasUsecase listMatriculasUsecase,
            UpdateMatriculasUsecase updateMatriculasUsecase,
            RegisterProfesorUsecase registerProfesorUsecase,
            CreateAsignaturaProfesorUsecase createAsignaturaProfesorUsecase,
            ResponseMappers responseMappers,
            Validator validator)
    {
        this.getPerfilUsecase = getPerfilUsecase;
        this.syncUsuarioUsecase = syncUsuarioUsecase;
        this.listMatriculasUsecase = listMatriculasUsecase;
        this.updateMatriculasUsecase = updateMatriculasUsecase;
        this.registerProfesorUsecase = registerProfesorUsecase;
        this.createAsignaturaProfesorUsecase = createAsignaturaProfesorUsecase;
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

    @GET
    @Path("mis-asignaturas")
    public List<AsignaturaResponse> misAsignaturas()
    {
        return listMatriculasUsecase.execute();
    }

    @PUT
    @Path("mis-asignaturas")
    public List<AsignaturaResponse> actualizarMisAsignaturas(@Valid ActualizarMatriculasRequest request)
    {
        return updateMatriculasUsecase.execute(request.getAsignaturaIds());
    }

    @POST
    @Path("asignaturas")
    public Response crearAsignatura(@Valid CreateAsignaturaProfesorRequest request)
    {
        AsignaturaResponse created = createAsignaturaProfesorUsecase.execute(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("registro-profesor")
    public Response registroProfesor(@Valid RegistroProfesorRequest request)
    {
        Usuario usuario = registerProfesorUsecase.execute(request);
        return Response.ok(responseMappers.toUsuarioPerfilResponse(usuario)).build();
    }
}
