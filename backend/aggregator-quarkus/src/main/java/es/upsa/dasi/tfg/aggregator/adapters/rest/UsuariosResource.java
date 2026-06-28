package es.upsa.dasi.tfg.aggregator.adapters.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ActualizarMatriculasRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.CreateAsignaturaProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.RegistroProfesorRequest;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Usuarios", description = "Perfil, matrículas y datos del usuario")
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

    @GET
    @Path("mis-asignaturas")
    public List<AsignaturaResponse> misAsignaturas() throws TfgException {
        return repository.findMisAsignaturas();
    }

    @PUT
    @Path("mis-asignaturas")
    public List<AsignaturaResponse> actualizarMisAsignaturas(@Valid ActualizarMatriculasRequest request)
            throws TfgException {
        return repository.updateMisAsignaturas(request);
    }

    @POST
    @Path("registro-profesor")
    public Response registroProfesor(@Valid RegistroProfesorRequest request) throws TfgException {
        return repository.registroProfesor(request);
    }

    @POST
    @Path("asignaturas")
    public Response crearAsignatura(@Valid CreateAsignaturaProfesorRequest request) throws TfgException {
        return repository.createAsignaturaProfesor(request);
    }
}
