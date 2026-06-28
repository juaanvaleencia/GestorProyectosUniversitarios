package es.upsa.dasi.tfg.aggregator.infrastructure.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.MiembroInvite;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.health.HealthClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.informes.InformesClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.mappers.RepositoryMapper;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.proyectos.ProyectosClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.tareas.TareasClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.usuarios.UsuariosClient;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositoryImpl implements Repository
{
    @Inject @RestClient HealthClient healthClient;
    @Inject @RestClient ProyectosClient proyectosClient;
    @Inject @RestClient TareasClient tareasClient;
    @Inject @RestClient UsuariosClient usuariosClient;
    @Inject @RestClient InformesClient informesClient;
    @Inject RepositoryMapper mapper;

    @Override
    public HealthResponse findHealth() {
        return healthClient.health();
    }

    @Override
    public List<ProyectoResponse> findProyectos() throws TfgException {
        return proyectosClient.list();
    }

    @Override
    public Proyecto createProyecto(ProyectoPost request) throws TfgException {
        return mapper.toProyecto(proyectosClient.create(request));
    }

    @Override
    public Proyecto createProyectoDesdePlantilla(long plantillaId) throws TfgException {
        return mapper.toProyecto(proyectosClient.createFromPlantilla(plantillaId));
    }

    @Override
    public Optional<Proyecto> findProyectoById(long id) throws TfgException {
        try {
            return Optional.of(mapper.toProyecto(proyectosClient.get(id)));
        }
        catch (NotFoundTfgException notFoundTfgException) {
            return Optional.empty();
        }
    }

    @Override
    public void updateProyecto(long id, ProyectoPut request) throws TfgException {
        proyectosClient.update(id, request);
    }

    @Override
    public void removeProyectoById(long id) throws TfgException {
        proyectosClient.delete(id);
    }

    @Override
    public List<HitoResponse> findHitosByProyecto(long proyectoId) throws TfgException {
        return proyectosClient.hitos(proyectoId);
    }

    @Override
    public HitoResponse createHito(long proyectoId, HitoPost request) throws TfgException {
        return proyectosClient.createHito(proyectoId, request);
    }

    @Override
    public void updateHito(long proyectoId, long hitoId, HitoPut request) throws TfgException {
        proyectosClient.updateHito(proyectoId, hitoId, request);
    }

    @Override
    public void removeHitoById(long proyectoId, long hitoId) throws TfgException {
        proyectosClient.removeHito(proyectoId, hitoId);
    }

    @Override
    public List<MiembroResponse> findMiembrosByProyecto(long proyectoId) throws TfgException {
        return proyectosClient.miembros(proyectoId);
    }

    @Override
    public InvitacionProyectoResponse inviteMiembro(long proyectoId, MiembroInvite request) throws TfgException {
        return proyectosClient.inviteMiembro(proyectoId, request);
    }

    @Override
    public void removeMiembroById(long proyectoId, long miembroId) throws TfgException {
        proyectosClient.removeMiembro(proyectoId, miembroId);
    }

    @Override
    public void abandonProyecto(long proyectoId) throws TfgException {
        proyectosClient.abandonProyecto(proyectoId);
    }

    @Override
    public List<InvitacionProyectoResponse> findInvitacionesByProyecto(long proyectoId) throws TfgException {
        return proyectosClient.invitaciones(proyectoId);
    }

    @Override
    public void acceptInvitacion(long invitacionId) throws TfgException {
        proyectosClient.acceptInvitacion(invitacionId);
    }

    @Override
    public void rejectInvitacion(long invitacionId) throws TfgException {
        proyectosClient.rejectInvitacion(invitacionId);
    }

    @Override
    public List<TareaResponse> findTareasByProyecto(long proyectoId) throws TfgException {
        return tareasClient.listByProyecto(proyectoId);
    }

    @Override
    public TareaFullResponse createTarea(long proyectoId, TareaPost request) throws TfgException {
        return tareasClient.create(proyectoId, request);
    }

    @Override
    public Optional<TareaFullResponse> findTareaById(long proyectoId, long id) throws TfgException {
        try {
            return Optional.of(tareasClient.get(proyectoId, id));
        }
        catch (NotFoundTfgException notFoundTfgException) {
            return Optional.empty();
        }
    }

    @Override
    public void updateTarea(long proyectoId, long id, TareaPut request) throws TfgException {
        tareasClient.update(proyectoId, id, request);
    }

    @Override
    public void removeTareaById(long proyectoId, long id) throws TfgException {
        tareasClient.delete(proyectoId, id);
    }

    @Override
    public Response syncUsuario(UsuarioSync request) throws TfgException {
        return usuariosClient.sync(request);
    }

    @Override
    public UsuarioPerfilResponse findPerfilUsuario() throws TfgException {
        UsuarioPerfilResponse perfil = usuariosClient.perfil();
        try {
            perfil.setParticipaciones(proyectosClient.participaciones());
        }
        catch (TfgException e) {
            perfil.setParticipaciones(List.of());
        }
        return perfil;
    }

    @Override
    public List<AsignaturaResponse> findMisAsignaturas() throws TfgException {
        return usuariosClient.misAsignaturas();
    }

    @Override
    public List<AsignaturaResponse> updateMisAsignaturas(ActualizarMatriculasRequest request) throws TfgException {
        return usuariosClient.actualizarMisAsignaturas(request);
    }

    @Override
    public Response registroProfesor(RegistroProfesorRequest request) throws TfgException {
        return usuariosClient.registroProfesor(request);
    }

    @Override
    public Response createAsignaturaProfesor(CreateAsignaturaProfesorRequest request) throws TfgException {
        return usuariosClient.crearAsignatura(request);
    }

    @Override
    public List<NotificacionResponse> findNotificaciones() throws TfgException {
        return usuariosClient.listNotificaciones();
    }

    @Override
    public List<UniversidadResponse> findUniversidades() throws TfgException {
        return usuariosClient.listUniversidades();
    }

    @Override
    public List<AsignaturaResponse> findAsignaturasByUniversidad(long universidadId) throws TfgException {
        return proyectosClient.asignaturas(universidadId);
    }

    @Override
    public List<PlantillaProyectoResponse> findPlantillasByAsignatura(long asignaturaId) throws TfgException {
        return proyectosClient.plantillas(asignaturaId);
    }

    @Override
    public PlantillaProyectoDetalleResponse findPlantillaById(long plantillaId) throws TfgException {
        return proyectosClient.plantilla(plantillaId);
    }

    @Override
    public PlantillaProyectoDetalleResponse updatePlantilla(long plantillaId, CreatePlantillaProyectoRequest request)
            throws TfgException
    {
        return proyectosClient.updatePlantilla(plantillaId, request);
    }

    @Override
    public PlantillaProyectoDetalleResponse createPlantilla(long asignaturaId, CreatePlantillaProyectoRequest request)
            throws TfgException
    {
        return proyectosClient.createPlantilla(asignaturaId, request);
    }

    @Override
    public List<ProyectoSupervisionResponse> findProyectosSupervisionByAsignatura(long asignaturaId)
            throws TfgException
    {
        return proyectosClient.proyectosSupervision(asignaturaId);
    }

    @Override
    public List<ProyectoGrupoSupervisionResponse> findGruposByPlantilla(long plantillaId) throws TfgException
    {
        return proyectosClient.gruposPorPlantilla(plantillaId);
    }

    @Override
    public List<AlumnoMatriculadoResponse> findAlumnosMatriculados() throws TfgException
    {
        return usuariosClient.listAlumnosMatriculados();
    }

    @Override
    public AlumnoPerfilSupervisionResponse findAlumnoPerfil(String alumnoUid) throws TfgException
    {
        AlumnoPerfilSupervisionResponse perfil = usuariosClient.alumnoPerfil(alumnoUid);
        try {
            perfil.setParticipaciones(proyectosClient.participacionesAlumno(alumnoUid));
        } catch (TfgException e) {
            perfil.setParticipaciones(List.of());
        }
        return perfil;
    }

    @Override
    public InformesResumenResponse findInformesResumen() throws TfgException {
        return informesClient.resumen();
    }

    @Override
    public DashboardResumenResponse findDashboardResumen() throws TfgException {
        return DashboardResumenResponse.builder()
                .proyectos(findProyectos())
                .informes(findInformesResumen())
                .build();
    }
}
